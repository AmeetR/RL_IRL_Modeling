# q learning
from pyqlearning.qlearning.boltzmann_q_learning import BoltzmannQLearning
from pyqlearning.qlearning.greedy_q_learning import GreedyQLearning
# testing
import unittest
# data
import numpy as np
# package
from utils import FallThroughQueue


class AdaptiveBetaQLearning(BoltzmannQLearning):

    def __init__(self, buffer=5, sigma=1.):
        super().__init__(self)
        self.TDE = FallThroughQueue(capacity=buffer)
        self.sigma = sigma

    def __calculate_sigmoid(self):
        """
        SOFTMAX VBDE: f(s, a, \sigma) = \frac{2e^{-|\alpha TDE| / \sigma}}{1-e^{-|\alpha TDE|/\sigma}}
        :return:
        """
        temp = np.exp(-self.TDE.avg() / self.sigma)
        return temp / (1-temp)

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
        self.TDE = self.gamma_value * next_max_q - self.extract_q_df(state_key, action_key)
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
        self.TDE = self.gamma_value * next_max_q - self.extract_q_df(state_key, action_key)
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
