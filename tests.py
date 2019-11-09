import unittest
from models import *




def calculate_sigmoid(alpha_value = 1, TDE = 0, sigma=.1):
	"""
	SOFTMAX VBDE: f(s, a, \sigma) = \frac{2e^{-|\alpha TDE| / \sigma}}{1-e^{-|\alpha TDE|/\sigma}}
	:return:
	"""
	temp = np.exp(-np.abs(alpha_value *TDE + 1e-16) / sigma)
	return 2*temp / (1-temp)




class sigmoid_tests(unittest.TestCase):
	def test1(self):
		self.assertTrue(np.allclose(3.082988165073595, calculate_sigmoid(.1, .5, .1)))

	def test2(self):
		self.assertTrue(np.allclose(21.01536928, calculate_sigmoid(.01, .99999, .11)))

	def test3(self):
		self.assertTrue(np.allclose(1099.000304, calculate_sigmoid(.999999999, .001, .55)));


if __name__ == '__main__':
    unittest.main()

