# q learning
from pyqlearning.qlearning.boltzmann_q_learning import BoltzmannQLearning
from pyqlearning.qlearning.greedy_q_learning import GreedyQLearning
# testing
import unittest
# data
import numpy as np
# package
from utils import FIFOQueue, EPSILON


class AdaptiveBetaQLearning(BoltzmannQLearning):

    def __init__(self, buffer_size=5, sigma=1., delta=1, inverse_temp=True):
        """
        :param buffer: buffer size for historical TDE
        :param sigma: sigma parameter for beta function
        :param delta: delta parater for beta update, default 1: fastest adaptation of exploration
        :param inverse_temp: whether optimizes for beta (inverse temperature) or for sigmoid (temperature
        parameter)
        """
        super().__init__(self)
        self.TDE = FIFOQueue(capacity=buffer_size)
        self.sigma = sigma
        if inverse_temp:
            self.__to_sigmoid = lambda x: 1/x
            self.update_exploration = self.__update_beta
            self.explore = EPSILON
        else:
            self.__to_sigmoid = lambda x: EPSILON if x == 0 else x
            self.update_exploration = self.__update_temp
            self.explore = 1
        self.delta = delta

    def __update_temp(self):
        self.explore += self.delta * (np.exp(self.sigma * self.TDE.abs_avg()) - 1 - self.explore)

    def __update_beta(self):
        self.explore += self.delta * (1/(np.exp(self.sigma * self.TDE.abs_avg()) - 1 + EPSILON)-self.explore)

    def __calculate_sigmoid(self):
        """
        Function of temperature in BoltzmannQLearning. Modified here to
        SOFTMAX VBDE: f(s, \sigma) = \frac{e^{-\sigma |TDE|}}{1-e^{-\sigma |TDE|}}
        :return:
        """
        return self.__to_sigmoid(self.explore)

    def update_q(self, state_key, action_key, reward_value, next_max_q):
        '''
        Update Q-Value while at the mean time update
        Args:
            state_key:              The key of state.
            action_key:             The key of action.
            reward_value:           R-Value(Reward).
            next_max_q:             Maximum Q-Value.
        '''
        # Now Q-Value.
        q = self.extract_q_df(state_key, action_key)
        self.TDE.add(reward_value + self.gamma_value * next_max_q - q)
        self.update_exploration()
        super(BoltzmannQLearning).update_q(state_key, action_key, reward_value, next_max_q)


class AdaptiveEpsilonGreedy(GreedyQLearning):

    def __init__(self, sigma=1.):
        super().__init__(self)
        self.sigma = sigma

    def update_q(self, state_key, action_key, reward_value, next_max_q):
        '''
        Update Q-Value.
        Args:
            state_key:              The key of state.
            action_key:             The key of action.
            reward_value:           R-Value(Reward).
            next_max_q:             Maximum Q-Value.
        '''
        # Now Q-Value.
        self.TDE = reward_value + self.gamma_value * next_max_q - self.extract_q_df(state_key, action_key)
        super(BoltzmannQLearning).update_q(state_key, action_key, reward_value, next_max_q)

    def get_epsilon_greedy_rate(self):
        ''' getter '''
        temp = np.exp(-np.abs(self.TDE) / self.sigma)
        return (1-temp) / (1+ temp)


class sigmoid_tests(unittest.TestCase):
    def test_trivial(self):
        test = AdaptiveBetaQLearning()
        self.assertEqual(test.sigma, 1)



if __name__ == '__main__':
    unittest.main()
