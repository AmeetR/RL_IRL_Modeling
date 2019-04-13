import numpy as np
import matplotlib.pyplot as plt
import random
from Typing import Dict, Tuple



class plot_normal:
    """
    This is a very basic plotting class to plot normal 
    distributions along with a fit curve. Here's a standard
    use case: 
    normal = np.random.normal(mean,std,5000)
    w1,x1,z1 = plt.hist(normal, 100, density = True)
    hist = plot_normal(mean,std, x1)
    plot = hist.dist_curve()
    """

    def __init__(self, a1: float, b1: float, c1: float) -> None:
        self.a1 = a1
        self.b1 = b1
        self.c1 = c1
        
    def dist_curve(self):
        plt.plot(self.c1, 1/(self.b1 * np.sqrt(2 * np.pi)) *
            np.exp( - (self.c1 - self.a1)**2 / (2 * self.b1**2) ), linewidth=2, color='y')
        plt.show()




