import numpy as np
from typing import *
import random
import matplotlib.pyplot as plt
from util import *


class Parameters:
    """
    This is just a shell class to hold the parameters
    """
    learning_rate: float = 0 #alpha, how quick the agent learns
    discount_factor: float = 0 # discount rate
    exploration_prob: float = 0 #epsilon
    def __init__(self):
        self.learning_rate = lambda: random.uniform(0, 1)
        self.discount_faction = random.uniform(0, 1)
        self.discount_faction = random.uniform(0, 1)
    def set_params(self, learning_rate: float, discount_factor: float) -> None:
        self.learning_rate = learning_rate
        self.discount_factor = discount_factor
        
    def set_learning_rate(self, alpha: float) -> None:
        self.learning_rate = alpha
        
    def set_discount_factor(self, gamma: float) -> None: 
        self.discount_factor = gamma
        

    def set_random_params(self) -> None:
        self.__init__()


class q_learning_agent:
    q_values: Dict[Tuple[str, str], float] = {}
    task: Task = None
    current_state: str = None
        
    def __init__(self, iterations: int = 1000, task: Task = None, initial_state: str = None, \
                 parameters: Parameters = Parameters()) -> None:
        self.iterations = iterations
        self.parameters = parameters
        self.task = task
        self.current_state =initial_state 
        self.initialize_q_params()
        
    def initialize_q_params(self) -> None:
        for key in self.task.transitions:
            self.q_values[key] = 0
                
    def update_q_values(self, state, action, next_state, reward, iteration) -> None: 
        alpha = self.parameters.learning_rate(iteration)
        gamma = self.parameters.discount_factor
        next_state_q_vals = {key:value for (key,value) in self.q_values.items() if next_state in key}
        old_q = self.q_values[(state, action)]
        new_q = (1-alpha) * old_q + alpha * (reward + gamma *\
                                             next_state_q_vals[max(next_state_q_vals, key = next_state_q_vals.get)])
        self.q_values[(state, action)] = new_q
        
                
    def choose_action(self, state) -> str:
        legal_actions = self.task.get_legal_actions(state)
        num = random.uniform(0, 1)
        if num <= self.parameters.exploration_prob:
            return random.choice(legal_actions)
        
        state_q_values =  {key:value for (key,value) in self.q_values.items() if state in key}
        return max(self.q_values, key= self.q_values.get)[1]
        
    def run_q_learning(self) -> list:
        actions: List[str]  = []
        i: int = 0
        while i <= self.iterations:
            i += 1
            action = self.choose_action(self.current_state)
            actions.append(action)
            next_state, reward = task.make_action(self.current_state, action)
            self.update_q_values(self.current_state, action, next_state, reward, iteration = i)
            print("The current state is: " + self.current_state)
            print("The given action is: " + action)
            print("The q values are as follows:" +str(self.q_values))
            print("Reward for next state is: " + str(reward))
            print("\n\n\n")
            self.current_state = next_state
        
        return actions




if __name__ == '__main__':
	states = ["center"]
	actions = {"center": ["left", "right", "nothing"]}
	rewards = {("center", "left"): lambda : np.random.choice([10, -1], p = [.8, .2]), ("center", "right"): lambda : np.random.choice([10, -1], p = [.2, .8]), ("center", "nothing"): -1}
	transitions = {("center", "left"): "center", ("center", "right"): "center", ("center", "nothing"): "center"}
	task = Task(states, rewards, transitions)

	qlearn = q_learning_agent(task = task, initial_state= "center", iterations = 10000 )
	qlearn.parameters.exploration_prob = .7
	qlearn.parameters.set_learning_rate(lambda x: 1/(x+1))
	qlearn.run_q_learning()








