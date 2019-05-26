import numpy as np
from typing import *
import random
import matplotlib.pyplot as plt
from util import *
from base_q_learning import *
import math

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
        self.sigma = 1
    def set_params(self, learning_rate: float, discount_factor: float) -> None:
        self.learning_rate = learning_rate
        self.discount_factor = discount_factor
        
    def set_learning_rate(self, alpha: float) -> None:
        self.learning_rate = alpha
        
    def set_discount_factor(self, gamma: float) -> None: 
        self.discount_factor = gamma
        

    def set_random_params(self) -> None:
        self.__init__()



class VBDE_Boltzmann_Softmax(q_learning_agent):
    
    def __init__(self, *args, **kwargs):
        super(VBDE_Boltzmann_Softmax, self).__init__(*args, **kwargs)
        self.softmax = self.stable_softmax
        self.all_beta = []
    
    def update_param(self, state, action, next_state, reward, iteration): 
        """
        This function will update beta. 
        """
        self.all_beta.append(self.beta)
        xi = 1e-16 # For handling zero devision, xi < lowest possible alpha * TD
        k = 1 # parameter to control the flatness of the beta curve, smaller k means slower exploitation decay as "surprise" goes larger
        alpha = self.parameters.learning_rate(iteration)
        sigma = self.parameters.sigma
        gamma = self.parameters.discount_factor
        error = reward + gamma*self.q_values[(next_state, action)] -self.q_values[(state, action)]
        overall_error = - k * np.abs(alpha * error)/ sigma
        
        f = (2*np.exp(overall_error))/(1-np.exp(overall_error)+xi)
        delta = self.parameters.delta

        self.beta[next_state] = (delta * f)+ ((1-delta) * self.beta[state])

        
    def initialize_q_params(self) -> None:
        #for key in self.task.transitions:
        #    self.q_values[key] = 0
        for state in self.task.states:
            for actions in self.task.actions:
                self.q_values[(state,actions)] = 0
                
        self.all_q_vals = []
        self.action_probs = []
        self.beta: Dict[str, float] = {}
        for state in self.task.states: 
            self.beta[state] = 1
        self.all_beta = []
    def update_q_values(self, state, action, next_state, reward, iteration) -> None: 
        alpha = self.parameters.learning_rate(iteration)
        gamma = self.parameters.discount_factor
        next_state_q_vals = {key:value for (key,value) in self.q_values.items() if next_state in key}
        old_q = self.q_values[(state, action)]
        new_q = (1-alpha) * old_q + alpha * (reward + gamma *\
                                             next_state_q_vals[max(next_state_q_vals, key = next_state_q_vals.get)])
        self.q_values[(state, action)] = new_q
        self.update_param(state, action, next_state, reward, iteration)
            
    def choose_action(self, state) -> str: 
        legal_actions = self.task.get_legal_actions(state)
        qvals = {key:value for (key,value) in self.q_values.items() if state in key}
        softmax_probs = self.stable_softmax(qvals, state)

        prob_list = []
        key_list = []
        for key in qvals:
            prob_list.append(softmax_probs[key])
            key_list.append(key[1])
        #for i in range(len(prob_list)): 
        #    if np.isnan(prob_list[i]):
        #        prob_list[i] = 1
        self.action_probs.append({key: np.log(value) for (key, value) in softmax_probs.items()})
        return np.random.choice(key_list, p = prob_list)
            
        
    def direct_softmax(self, qvals, state):
        for key in qvals: 
            qvals[key] = qvals[key]
            
        beta_val = self.beta[state]

        sum_qs= sum([np.exp(qvals[x]*beta_val) for x in qvals])

        
        softmax_dict = {}
        for key in qvals:
            curr_ex = np.exp(qvals[key]*beta_val)
            softmax_dict[key] = curr_ex/sum_qs

            #print(softmax_dict)
        return softmax_dict
    
    def stable_softmax(self, qvals, state):
        # USE Stable softmax instead as it protects against numerical instability
        beta_val = self.beta[state]
        max_q = max(qvals.values())
        sum_qs= sum([np.exp((qvals[x] - max_q)*beta_val) for x in qvals])

        
        softmax_dict = {}
        for key in qvals:
            curr_ex = np.exp((qvals[key] - max_q)*beta_val)
            softmax_dict[key] = curr_ex/sum_qs

            #print(softmax_dict)
        #print("Probability", softmax_dict)
        return softmax_dict
    def AIC(self, likelihood):
        num_parameters = 3
        return 2*num_parameters - 2*likelihood