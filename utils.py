from collections import deque
import numpy as np


class FallThroughQueue:

    def __init__(self, capacity):
        self.cap = capacity
        self.size = 0
        self.data = deque()

    def add(self, item):
        if self.is_full():
            self.data.popleft()
        self.data.append(item)

    def size(self):
        return self.size

    def avg(self):
        return np.mean(self.data)

    def is_empty(self):
        return self.size == 0

    def is_full(self):
        return self.size == self.cap
