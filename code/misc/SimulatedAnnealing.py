import math
import random


class SimulatedAnnealing:
    def __init__(self, start_state, start_temp, iterations, alpha):
        self.start_state = start_state
        self.start_temp = start_temp
        self.iterations = iterations
        self.alpha = alpha

    def anneal(self):
        objective = ObjectiveFunction()
        curr_state = self.start_state
        curr_score = objective.process_score(self.start_state, 0)
        temperature = self.cooling_schedule()

        for i in range(self.iterations):
            neighbor_state = curr_state.generate_neighbor()
            neighbor_score = objective.process_score(neighbor_state, i)
            p = self.prob(curr_score, neighbor_score, temperature.__next__()) #i/self.iterations

            if p > random.random():
                curr_state = neighbor_state
                curr_score = neighbor_score

        return objective.best_state, objective.best_score

    def cooling_schedule(self):
        temp = self.start_temp
        while True:
            yield temp
            temp *= self.alpha

    def prob(self, curr_score, new_score, temp):
        if new_score > curr_score:
            return 1.0
        else:
            return math.exp(-abs(new_score - curr_score) / (1 + temp))


class ObjectiveFunction:
    def __init__(self):
        self.best_state = None
        self.best_score = float('-inf')

    def process_score(self, state, i):
        score = state.calc_score()
        if score > self.best_score:
            #print("iteration:" + str(i))
            #print("prev best:" + str(self.best_score))
            #print("new best:" + str(score))
            self.best_score = score
            self.best_state = state.copy()  # POSSIBLY DONT NEED TO COPY, DEPENDS ON IMPLEMTATION
        return score




    # GREEDY APPROACH FOR GENERATING NEIGHBOR #TODO TRY ONCE CODE IS WORKING
    # def generate_change(self, student):
    #    valid_busses = set()
    #    for bus in self.busses:
    #        if not student.previous_buses.contains(bus):
    #            valid_busses.add(bus)
    #    optimal_bus = None
    #    optimal_score = 0
    #    for bus in valid_busses:
    #        if b.size < b.bus_size:
    #            curr_score = len(b.get_friends(student))
    #            if curr_score > optimal_score:
    #                optimal_score = curr_score
    #                optimal_bus = bus
    #    optimal_bus.add_student(student)