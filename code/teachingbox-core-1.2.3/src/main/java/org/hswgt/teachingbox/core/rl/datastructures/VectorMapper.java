
package org.hswgt.teachingbox.core.rl.datastructures;

import cern.colt.matrix.impl.DenseDoubleMatrix1D;

/**
 * Implementing classes realize the mapping an of arbitrary vector (or state) of n
 * dimensions to a new one of m dimensions (m may be &lt;&gt;= n). This is needed
 * in several cases, i.e.: 
 * 
 * - we want to encode an angle alpha (i.e. 0-2PI), but due to further use we can't
 * provide such a jumping state variable (jump between 2PI and 0), so for this state
 * dimension we provide 2 dimensions cos(alpha) and sin(alpha) instead.
 * 
 * - we want to model environment dynamics for an environment with n states by Locally
 * Weighted Regression, but we now, that for the state prediction one dimension is not 
 * needed (not causal, but needed in further learning processes). To avoid opening an
 * unnecessary dimension for regression, we map each incoming state to estimate into a
 * new one without the mentioned dimension.
 *
 * @author Richard Cubek
 * 
 */
public interface VectorMapper {
    /**
     * This methods maps an arbitrary vector of n dimensions to a new one
     * of (arbitrary) m dimensions.
     * @param vector The vector to map.
     * @return The mapped (new) vector.
     */
    public DenseDoubleMatrix1D getMappedVector(DenseDoubleMatrix1D vector);
}
