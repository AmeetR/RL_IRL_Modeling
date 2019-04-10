/**
 *
 * $Id: RadialBasisFunction.java 475 2009-12-15 09:10:57Z Markus Schneider $
 *
 * @version $Rev: 475 $
 * @author $Author: Markus Schneider $
 * @date $Date: 2009-12-15 10:10:57 +0100 (Di, 15. Dez 2009) $
 *
 */

package org.hswgt.teachingbox.core.rl.network.rbf;
import org.hswgt.teachingbox.core.rl.network.*;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;

/**
 * A Radial Basis Function with mean an derivation as well as a scaling factor
 *
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
    // create new rbf with scale=1, mu=0 and sigma=2.
    RadialBasisFunction rbf = new RadialBasisFunction(0,2);
    // get the value at 1.0
    double v = getValue(1.0);
    </pre>
 */
public class RadialBasisFunction extends NetworkNode implements java.io.Serializable {
    private static final long serialVersionUID = 6797504239900972793L;
    protected DoubleMatrix1D sigma;
    protected double scale = 1;
    protected double maxDistance = 10;

    public RadialBasisFunction() {
    }

    /**
     * Constructor
     * Creates a new RBF with center mu and width sigma
     * @param mu Center
     * @param sigma Width
     */
    public RadialBasisFunction(DoubleMatrix1D mu, DoubleMatrix1D sigma) {
        if( mu.size() != sigma.size() ) {
            throw new IllegalArgumentException("mu and sigma must have the" +
                    "same size");
        }

        this.setPosition(mu);
        this.setSigma(sigma);
        this.dimension = mu.size();
    }

    /**
     * Constructor
     * Creates a new RBF with center mu and width sigma
     * @param mu Center
     * @param sigma Width
     */
    public RadialBasisFunction(double[] mu, double[] sigma) {
        this(new DenseDoubleMatrix1D(mu), new DenseDoubleMatrix1D(sigma));
    }

    /**
     * Constructor
     * Creates a new RBF with center mu and width sigma and scaling factor scale
     * @param mu Center
     * @param sigma Width
     * @param scale scaling factor
     */
    public RadialBasisFunction(DoubleMatrix1D mu, DoubleMatrix1D sigma,
            double scale) {
        this(mu, sigma);
        this.setScale(scale);
    }

    /**
     * Constructor
     * Creates a new RBF with center mu and width sigma and scaling factor scale
     * @param mu Center
     * @param sigma Width
     * @param scale scaling factor
     */
    public RadialBasisFunction(double[] mu, double[] sigma, double scale) {
        this(new DenseDoubleMatrix1D(mu), new DenseDoubleMatrix1D(sigma), scale);
    }

    /**
     * Copy Constructor
     * 
     * @param other The rbf object
     */
    public RadialBasisFunction(final RadialBasisFunction other) {
        this(other.position, other.sigma, other.scale);
    }

    /**
     * Calculates <code>scale*exp( - sum((x-mu).^2 ./ (2.*sigma.^2)) );</code>
     *
     * @param x The input
     * @return <code>scale*exp( - sum((x-mu).^2 ./ (2.*sigma.^2)) );</code>
     */
    public double getValue(final DoubleMatrix1D x) {
        return this.getValue(x, this.position, this.sigma, this.scale, this.maxDistance);
    }

    /**
     * Calculates <code>scale*exp( - sum((x-mu).^2 ./ (2.*sigma.^2)) );</code>
     *
     * @param x The input
     * @param mu The location of the center
     * @param sigma the width ot the function
     * @param scale the scaling factor
     * @param maxDistance The maximum distance
     * @return <code>scale*exp( - sum((x-mu).^2 ./ (2.*sigma.^2)) );</code>
     */
    public double getValue(final DoubleMatrix1D x, double[] mu,
            double[] sigma, double scale, double maxDistance) {
        if( (mu.length != sigma.length) || mu.length != x.size() ) {
            throw new IllegalArgumentException("x, mu and sigma must have the same size");
        }

        double sum = 0;
        for(int i=0; i<mu.length; i=i+1) {
            double dist = mu[i] - x.get(i);
            sum += (dist*dist) / (2*sigma[i]*sigma[i]);
        }

        // ignore values that have greater distance than the ellipse with 10*sigma
        // only if the network is non-normalized
        if( this.getNet() != null && !this.getNet().isNormalized()
                && sum > maxDistance )
            return 0;

        return scale*Math.exp(-sum);
    }

    /**
     * Calculates <code>scale*exp( - sum((x-mu).^2 ./ (2.*sigma.^2)) );</code>
     *
     * @param x The input
     * @param mu The location of the center
     * @param sigma the width ot the function
     * @param scale the scaling factor
     * @param maxDistance The maximum distance
     * @return <code>scale*exp( - sum((x-mu).^2 ./ (2.*sigma.^2)) );</code>
     */
    public double getValue(final DoubleMatrix1D x,
            final DoubleMatrix1D mu, final DoubleMatrix1D sigma,
            final double scale, double maxDistance) {
        if( (mu.size()!= sigma.size()) || mu.size() != x.size()) {
            throw new IllegalArgumentException("x, mu and sigma must have the same size");
        }

        double sum = 0;
        for(int i=0; i<mu.size(); i=i+1) {
            double dist = mu.get(i) - x.get(i);
            sum += (dist*dist) / (2*sigma.get(i)*sigma.get(i));
        }

        // ignore values that have greater distance than the ellipse with 10*sigma
        // only cut the RBF if we don't use a normalized net!
        if( this.getNet() != null && !this.getNet().isNormalized()
                && sum > maxDistance )
            return 0;

        return scale*Math.exp(-sum);
    }

    /**
     * Calculates <code>scale*exp( - sum((x-mu).^2 ./ (2.*sigma.^2)) );</code>
     *
     * @param x The input
     * @param mu The location of the center
     * @param sigma the width ot the function
     * @param scale the scaling factor
     * @param maxDistance The maximum distance
     * @return <code>scale*exp( - sum((x-mu).^2 ./ (2.*sigma.^2)) );</code>
     */
    public double getValue(final double[] x, final double[] mu,
            final double[] sigma, final double scale, double maxDistance) {
        if( (mu.length != sigma.length) || mu.length != x.length ) {
            throw new IllegalArgumentException("x, mu and sigma must have the same size");
        }

        double sum = 0;
        for(int i=0; i<mu.length; i=i+1) {
            double dist = mu[i] - x[i];
            sum += (dist*dist) / (2*sigma[i]*sigma[i]);
        }

        // ignore values that have greater distance than the ellipse with 10*sigma
        // only if the network is non-normalized
        if( this.getNet() != null && !this.getNet().isNormalized()
                && sum > maxDistance )
            return 0;

        return scale*Math.exp(-sum);
    }

    /**
     * Calculates <code>scale*exp( - sum((x-mu).^2 ./ (2.*sigma.^2)) );</code>
     *
     * @param x The input
     * @param mu The location of the center
     * @param sigma the width ot the function
     * @param scale the scaling factor
     * @return <code>scale*exp( - sum((x-mu).^2 ./ (2.*sigma.^2)) );</code>
     */
    public static double getValue(final double x, double mu, double sigma,
            double scale) {
        return scale*Math.exp(-((mu - x)*(mu - x)) / (2*sigma*sigma));
    }

    /**
     * Creates a deep copy of the RBF
     * @return The copy
     */
    public RadialBasisFunction copy() {
        return new RadialBasisFunction(this.position, this.sigma, this.scale);
    }

    /**
     * @return the sigma
     */
    public DoubleMatrix1D getSigma() {
        return this.sigma.copy();
    }

    /**
     * @param sigma the sigma to set
     */
    public void setSigma(DoubleMatrix1D sigma) {
        boolean notify = true;
        if (this.sigma != null && this.sigma.equals(sigma))
            notify = false;
        
        this.sigma = sigma.copy();

        if (notify)
            this.notifyShapeChanged();
    }

    /**
     * returns the scaling factor
     * @return the scale
     */
    public double getScale() {
        return this.scale;
    }

    /**
     * @param scale the scaling factor to set
     */
    public void setScale(double scale) {
        boolean notify = true;
        if (this.scale == scale)
            notify = false;

        this.scale = scale;

        if (notify)
            this.notifyShapeChanged();
    }

    public double getMaxDistance() {
        return this.maxDistance;
    }

    public void setMaxDistance(double maxDistance) {
        this.maxDistance = maxDistance;
    }
}