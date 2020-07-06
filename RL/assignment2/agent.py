import numpy as np
class Agent:
    def __init__(self, Q, mode="mc_control", nA=6, alpha = 0.01, gamma = 0.99):
        self.Q = Q
        self.mode = mode
        self.nA = nA
        self.alpha = alpha
        self.gamma = gamma
        self.mc_step = 0
        self.mc_history = np.zeros((202,3)) - 1
        self.num_state = np.ones(501)


    def select_action(self, state, eps):
        pi = np.zeros(self.nA) + (eps/self.nA)
        max_q = np.max(self.Q[state])
        max_Q = (self.Q[state] == max_q).astype("uint8")

        count = np.sum(max_Q)
        max_Q = max_Q * (1 - eps) / count
        p = pi + max_Q

        action = np.random.choice(self.nA, p=p)
        return action

    def step(self, state, action, reward, next_state, done):
        if self.mode == "q_learning":
            if done:
                self.Q[state][action] += self.alpha * (reward-self.Q[state][action])
            else:
                act = self.select_action(next_state,0)
                self.Q[state][action] += self.alpha * (reward + self.gamma * self.Q[next_state][act] - self.Q[state][action])
            return

        if self.mode == "mc_control":
            if not done:
                self.mc_history[self.mc_step] = np.array([reward, state, action])
            else:
                for i in range(self.mc_step):
                    k = self.mc_step - i - 2
                    self.mc_history[k][0] += self.gamma * self.mc_history[k+1][0]

                for i in self.mc_history:
                    if i[1]!=-1:
                        self.Q[int(i[1])][int(i[2])] +=  (i[0] - self.Q[int(i[1])][int(i[2])])/self.num_state[int(i[1])]
                        self.num_state[int(i[1])] +=1
                    else:
                        break
                self.num_state = np.ones(501)
                self.mc_step=0
                self.mc_history = np.zeros((202,3)) - 1
            return