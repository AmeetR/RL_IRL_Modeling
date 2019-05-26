import numpy as np
from typing import *
import random
import matplotlib.pyplot as plt
from util import *
from base_q_learning import *

class Parameters:
    """
    This is just a shell class to hold the parameters
    """
    learning_rate: float = 0 #alpha, how quick the agent learns
    discount_factor: float = 0 # discount rate
    exploration_prob: float = 0 #epsilon
    tau: float = 0
    def __init__(self):
        self.learning_rate = lambda x: random.uniform(0, 1)
        self.discount_faction = random.uniform(0, 1)
        self.exploration_prob = random.uniform(0, 1)
        self.tau = np.random.exponential(5)
        self.delta = random.uniform(0,1)
        self.sigma = np.random.exponential(5)
    def set_params(self, learning_rate: float, discount_factor: float) -> None:
        self.learning_rate = learning_rate
        self.discount_factor = discount_factor
        
    def set_learning_rate(self, alpha: float) -> None:
        self.learning_rate = alpha
        
    def set_discount_factor(self, gamma: float) -> None: 
        self.discount_factor = gamma
        

    def set_random_params(self) -> None:
        self.__init__()


class Updating_Neuron:
    q_values: Dict[Tuple[str, str], float] = {}
    epsilon: Dict[str, float] = {}
    task: Task = None
    current_state: str = None
    all_q_vals: List[Dict] = []
    state_list: List[str] = []
    action_probs = []
    outcome: float = 0 # Cumulative Reward in each trial
    baseline: float = 0 # Approximate cumulative reward in next trial
    mu: float = 0
    sigma: float = 0
    def __init__(self, iterations: int = 1000, task: Task = None, initial_state: str = None, \
                 parameters: Parameters = Parameters()) -> None:
        self.iterations = iterations
        self.parameters = parameters
        self.task = task
        self.current_state =initial_state 
        self.initialize_q_params()
        self.mu = .5
        self.sigma = .25
        
    def initialize_q_params(self) -> None:
        #for key in self.task.transitions:
        #    self.q_values[key] = 0
        for state in self.task.states:
            for actions in self.task.actions:
                self.q_values[(state,actions)] = 0
                
        self.all_q_vals = []
        self.action_probs = []
        
        
                
    def update_q_values(self, state, action, next_state, reward, iteration) -> None: 
        alpha = self.parameters.learning_rate(iteration)
        gamma = self.parameters.discount_factor
        next_state_q_vals = {key:value for (key,value) in self.q_values.items() if next_state in key}
        old_q = self.q_values[(state, action)]
        new_q = (1-alpha) * old_q + alpha * (reward + gamma *\
                                             next_state_q_vals[max(next_state_q_vals, key = next_state_q_vals.get)])
        self.q_values[(state, action)] = new_q
        
        
    def update_param(self, epsilon, rewards):
        mu = self.mu
        sigma = self.sigma
        
        alpha = self.parameters.learning_rate(0)*(sigma**2)
        delta_mu = alpha * (self.baseline - rewards) * ((epsilon - mu)/(sigma**2)) 
        delta_sigma = alpha * (self.baseline - rewards) * (((epsilon - mu)**2-sigma**2)/(sigma**3))
        #print(delta_mu, delta_sigma)
        self.baseline = rewards + self.parameters.learning_rate(0)*(self.baseline - rewards)
        self.mu = min(self.mu + self.parameters.learning_rate(0) * delta_mu * rewards, 1e16)
        self.sigma = max(self.sigma + self.parameters.learning_rate(0) * delta_sigma * rewards, 0) + .0025

        
        
        
    def generate_action_probs(self, epsilon) -> None: 
        action_prob_dict: Dict[Tuple[str,str], float]
        action_prob_dict = {}
        
        for state in self.task.states: 
            curr_state_qvals = {key:value for (key,value) in self.q_values.items() if state in key}
            max_action = max(curr_state_qvals, key = curr_state_qvals.get)
            for key in curr_state_qvals: 
                if key[1] == max_action[1]:
                    action_prob_dict[key] = np.log(epsilon/len(self.task.actions) + (1-epsilon))
                else: 
                    action_prob_dict[key] = np.log(epsilon/len(self.task.actions))

        self.action_probs.append(action_prob_dict)
            
    def choose_action(self, state, epsilon) -> str:

        legal_actions = self.task.get_legal_actions(state)
        num = random.uniform(0, 1)
        if num <= epsilon:
            return random.choice(legal_actions)
        
        state_q_values =  {key:value for (key,value) in self.q_values.items() if state in key}
        return max(self.q_values, key= self.q_values.get)[1]
        
    def run_q_learning(self) -> list:
        self.actions = []
        actions: List[str]  = []
        i: int = 0
        while i < self.iterations:
            epsilon = np.random.normal(self.mu, self.sigma) % 1
            
            action = self.choose_action(self.current_state, epsilon)
            actions.append(action)
            next_state, reward = self.task.make_action(self.current_state, action, i)
            self.update_param(epsilon, reward)
            self.update_q_values(self.current_state, action, next_state, reward, iteration = i)
            self.all_q_vals.append(self.q_values)
            #print("The current state is: " + str(self.current_state))
            #print("The given action is: " + str(action))
            #print("The q values are as follows:" +str(self.q_values))
            #print("Reward for next state is: " + str(reward))
            #print("\n\n\n")
            self.generate_action_probs(epsilon)
            self.current_state = next_state
            self.actions.append(action)
            #print(i)
            i+=1
        return actions