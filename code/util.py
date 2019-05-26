import numpy as np
import matplotlib.pyplot as plt
import random
from typing import *



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





class Task: 
    """
    This class is to define an environment that the agent will act on. 
    """
    states: List[str] = []
    rewards: Dict[Tuple[str, str], Callable] = {} #mapping from state, action tuple to a function 
    transitions: Dict[Tuple[str, str], Callable] = {} # This is a mapping from state,action tuple to the new state
        
    def __init__(self, states: List[str], rewards: Dict[Tuple[str, str], Callable], transitions: Dict[Tuple[str, str], str]) -> None:
        self.states = states
        print(rewards)
        
        for key in rewards:
            print(key)
            typecheck = type(lambda : 10)
            if type(rewards[key]) != typecheck: 
                temp = float(rewards[key])
                func = lambda : temp
                rewards[key] = func
        
        self.rewards = rewards
        self.transitions = transitions
        
    def make_action(self, state: str, action: str) -> Tuple[str, float]: 
        """
        This function takes in a proposed state and action, executes it on 
        the environment, then returns a tuple with the next state as the 
        first value and the reward as the second value. 
        """
        reward: float = self.rewards[(state, action)]()
        new_state: str = self.transitions[(state,action)]
        return (new_state, reward)
    
    def get_legal_actions(self, state: str) -> List[str]:
        """
        This will return a list of legal actions from a given state.
        """
        actions: List[str] = []
        for key in self.transitions:
            if state in key: 
                actions.append(key[1])
        return actions


