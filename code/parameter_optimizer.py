from simanneal import Annealer
import numpy as np
import pandas as pd
import random
import matplotlib.pyplot as plt
import scipy.io as sio
from typing import Dict, Tuple
import pickle as pkl 
import sys
import base_q_learning as bq
from util import *
import editdistance as ed
from updating_softmax_function import *
from updating_epsilon_model import *
from egreedy_updating_normal import *

class data(list): 
    """
    This is a basic class to hold our data and check for equality 
    of a list of actions generated by the agent and generated by the 
    mouse. Later, there will more complex ways of scoring in this class
    """
    actions: np.matrix = None
    all_data = np.matrix = None
    def __init__(self, input_data: list, agent: bool = True): 
        if agent:
            self.actions = np.matrix(input_data)
        else:
            print("Please instantiate a mouse class instead of a data class")
        
        
    def __eq__(self, other): 
        for index in range(len(self.actions)):
            if self.actions[index] != other.actions[index]:
                return False
        return True
        

    def __ne__(self, other):
        return not self.__eq__(other)
    
    
    def score(self, d2):
        assert len(self.actions) == len(data.d2.actions)
        score = 0
        for i in range(len(self.actions)): 
            if self.actions[i] == d2.actions[i]: 
                score += 1
        return score/len(self.actions)
    
    def loss(self):
        """
        The default loss will be cross entropy loss: 
        
        We want to sum over the actions and calculate the -log likelihood
        of the given data (what we are optimizing for) under the generated model. 
        We possibly want to take the average over all of the actions. We can use q
        values as proxies for probabilites (that's effectively what they are)
        """
        pass
    
class mouse(data, Task): 
    def __init__(self, matlab_url):
        mat = sio.loadmat(matlab_url)
        self.result = mat['group_setsize2_result'][0][0][0][0:500]
        self.rewards = mat['group_setsize2_reward'][0][0][0][0:500]
        self.ports = mat['group_setsize2_portside'][0][0][0][0:500]
        self.odors = mat['group_setsize2_odors'][0]
        self.schedule = mat['group_setsize2_schedule'][0][0][0][0:500]
        self.all = list(zip(self.result, self.rewards, self.ports, self.schedule))[0:500]
        self.actions = list(set(self.ports))
        self.odors = [str(i[0]) for i in list(self.odors)]
        self.states = list(set(self.schedule))
        
    def make_action(self, state: str, action: str, i: int = 0) -> Tuple[str, float]:
        if str(self.ports[i]) == str(action): 
            reward: float = 10 
        else: 
            reward: float = 0
        new_state: str = self.schedule[i]
        return (new_state, reward)
    def get_legal_actions(self, state: str) -> List[str]:
        return self.actions
    
        
    


class parameter_optimizer(Annealer): 
    """
    This class will run simulated annealing
    in order to find the correct parameters for a single mouse
    """

    state = None
    def __init__(self, matlab_url):
        self.m = mouse(matlab_url)
        
        self.state = VDBE_Boltzmann(task = self.m, initial_state = self.m.schedule[0], iterations = len(self.m.all))
    def move (self):
        """
        Changes the parameters randomly
        """


        choices = [-1,1]
        alpha = self.state.parameters.learning_rate(0)
        self.state.parameters.learning_rate = lambda x: (alpha + random.choice(choices) * .05) % 1
        self.state.parameters.discount_faction =  (self.state.parameters.discount_faction + random.choice(choices) * .05) % 1
        self.state.parameters.exploration_prob = (self.state.parameters.exploration_prob + random.choice(choices) * .05) % 1
        self.state.parameters.tau = max(self.state.parameters.tau +  random.choice(choices) * .5, 0) + 1e-16
        self.state.parameters.delta = (self.state.parameters.delta + random.choice(choices) * .05) % 1
        self.state.parameters.sigma = max(self.state.parameters.sigma + random.choice(choices) *.5, 0) + 1e-16
        
    def energy(self):
        """
        count = 0
        for iteration in range(3):
            self.state.initialize_q_params()            
            self.state.run_q_learning()
            for i in range(0, len(self.m.all)):
                got_reward = bool(self.m.rewards[i])

                if got_reward:
                    mouse_action = self.m.ports[i]
                else:
                    mouse_action = int(list(set([a for a in self.m.ports if a!=self.m.ports[i]]))[0])

                if mouse_action != self.state.actions[i]:
                    count += 1
                
        count = count/3
        return count/len(self.m.all)
        -----------------------------------------------------------
        The above is straight comparison, the below is edit distance
        -----------------------------------------------------------
        mouse_actions: str = ""
        self.state.initialize_q_params()            
        self.state.run_q_learning()
        for i in range(0, len(self.m.all)):
            got_reward = bool(self.m.rewards[i])
            if got_reward:
                mouse_actions += str(self.m.ports[i])
            else: 
                mouse_actions += str(list(set([a for a in self.m.ports if a!=self.m.ports[i]]))[0])
        agent_actions = "".join([str(i) for i in self.state.actions])
        return ed.eval(mouse_actions, agent_actions) 
        -----------------------------------------------------------
        Below this, is MLE
        -----------------------------------------------------------
        """
        log_lik = 0
        self.state.initialize_q_params()            
        self.state.run_q_learning()
        for i in range(0,len(self.m.all)):
            state = self.m.schedule[i]
            action = self.m.ports[i]
            try:
                log_lik += self.state.action_probs[i][(state,action)]
            except Exception as e: 
                continue # log_lik += 0


        
        return -1*log_lik
                
        
if __name__ == '__main__':
    m = parameter_optimizer('../data/pilot_data_2odor_8020prob.mat')

    m.Tmax = 2500.0  # Max (starting) temperature
    m.Tmin = .25      # Min (ending) temperature
    m.steps = 1000   # Number of iterations
    m.updates = 100000   # Number of updates (by default an update prints to stdout)
    agent, energy = m.anneal()
    #df1 = pd.read_csv("../output/AIC.csv", index_col = None)

    #df = pd.DataFrame([["Standard Epsilon", agent.AIC(-1*energy)]], columns = ["Name", "AIC"])
    #df = pd.DataFrame([["Standard Epsilon", 100]], columns = ["Name", "AIC"])
    #df1.loc[len(df1) + 1] = pd.DataFrame([["test Epsilon", 90]], columns = ["Name", "AIC"])
    #print("Updating Softmax")
    #print(agent.AIC(-1*energy))
    #df1.loc[len(df1) + 1] = ["Updating Epsilon" , agent.AIC(-1*energy)]

    #df1.to_csv("../output/AIC.csv", index = False)
    #alpha = "alpha:" + str(agent.parameters.learning_rate(0)) + "\n"
    #discount = "gamma:" + str(agent.parameters.discount_faction)+ "\n"
    #delta = "delta: " + str(agent.parameters.delta)+ "\n"
    #sigma = "sigma: " + str(agent.parameters.sigma)+ "\n"
    #epsilon = "epsilon: " + str(agent.parameters.exploration_prob) + "\n"
    
    #score = "\nThe minimum score is: " + str(energy) + "\n\n\n"
    #allvals = "\n\n\n Here are all the q values: " + str(agent.all_q_vals)
    #print(alpha + discount + delta + sigma)
    with open("../output/Updating_Epsilon.txt", 'w') as outfile:
        outfile.write(str(agent.all_epsilons))

    



