/**
 *
 * $Id: RBFNetwork.java 475 2009-12-15 09:10:57Z Markus Schneider $
 *
 * @version $Rev: 475 $
 * @author $Author: Markus Schneider $
 * @date $Date: 2009-12-15 10:10:57 +0100 (Di, 15. Dez 2009) $
 *
 */

package org.hswgt.teachingbox.core.rl.network.rbf;

import org.hswgt.teachingbox.core.rl.network.*;
import cern.colt.matrix.DoubleMatrix1D;

/**
 * A network of radial basis function.
 * 
 * The resulting feature vector will at a position x is
 * the value of each Radial Basis Function at this position.
 * 
 * For example:
 * <pre>
 * feat(0) = rbf(0).getValue(x)
 * feat(1) = rbf(1).getValue(x)
 * ...
 * feat(n) = rbf(n).getValue(x)
 * </pre>
 *
 * So the length of the feature vector will be the number of
 * Radial Basis Function in the network.
 * 
 * Here is an example of a plotted network with 3 RBFs
 * <pre>
   1 ++--------+---------+---------+----------+---------+---------+--------++
     +         +        |+ |      |+ |       |+ |       +         +         +
     |                 +   +     +   +      +   +                           |
     |                +     +   +     +    +     +                          |
 0.8 ++               |      |  |      |   |      |                        ++
     |               +       + +       +  +       +                         |
     |               |       | |        | |       |                         |
     |              |         |         ||         |                        |
 0.6 ++             +         +          +         +                       ++
     |              |         ||        + |         |                       |
     |             +         + +       +  +         +                       |
     |             |         |  |      |   |         |                      |
 0.4 ++           +         +   +     +    +         +                     ++
     |            |         |    |    |     |         |                     |
     |           +         +     +   +      +         +                     |
     |           |         |      |  |       |         |                    |
 0.2 ++         +         +       + +        +         +                   ++
     |         +         +         +          +         +                   |
     |        +         +         + +          +         +                  |
     +      ++ +      ++ +      ++ + ++       + ++      + ++      +         +
   0 ++--------+---------+---------+----------+---------+---------+--------++
    -2        -1         0         1          2         3         4         5
    </pre>

    Example:
    <pre>
    RBFNetwork net = new RBFNetwork();
    // add new rbf with mu=0 and sigma=2.
    net.add( new RadialBasisFunction(0,2) );
    // add new rbf with mu=5 and sigma=2.
    net.add( new RadialBasisFunction(5,2) );
    // get value a position 1.0
    DoubleMatrix1D feat = net.getFeatures(new DenseDoubleMatrix1D( new double[] {1} ) );
    </pre>
 */

// TODO: Is this net really needed anymore?
public class RBFNetwork extends Network implements java.io.Serializable
{
    private static final long serialVersionUID = 2076205912927964302L;

    /**
     * The dimension of the mu/sigma vectors
     */
    protected int dimension = 0;

    /**
     * Constructor
     */
    public RBFNetwork()
    {
    }

    /**
     * Copy Constructor
     *
     * @param other Any RBFNetwork to copy
     */
    public RBFNetwork(final RBFNetwork other)
    {
        super(other);
        this.dimension = other.dimension;
    }

    /**
     * Adds a new RadialBasisFunction
     * @param rbf The RadialBasisFunction to add
     */
    public void add(final RadialBasisFunction rbf)
    {
        // if this is the first rbf, then set the dimension
        // of the network to the dimension of the rbf
        if( this.dimension == 0 ){
            this.dimension = rbf.getDimension();
        }

        if( this.dimension != rbf.getDimension() ){
            throw new IllegalArgumentException("The network and the rbf must have the same dimension");
        }

        net.add(rbf);
        notifyFeatureVectorSizeChanged(this.getFeatureVectorSize());
    }

    /**
     * Add a new RadialBasisFunction to the network
     *
     * @param mu Center
     * @param sigma Width
     */
    public void add(final DoubleMatrix1D mu, final DoubleMatrix1D sigma)
    {
        add(new RadialBasisFunction(mu, sigma));
    }

    /**
     * Add a new RadialBasisFunction to the network
     *
     * @param mu Center
     * @param sigma Width
     */
    public void add(final double[] mu, final double[] sigma)
    {
        add(new RadialBasisFunction(mu, sigma));
    }

    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.feature.FeatureModifier#copy()
     */
    @Override
    public RBFNetwork copy()
    {
        return new RBFNetwork(this);
    }
}
