import numpy as np


def policy_evaluation(env, policy, gamma=0.99, theta=1e-8):
    V = np.zeros(env.nS)
    delta=1.1
    while (delta > theta):
        v = V.copy()
        V = np.zeros(env.nS)
        map =[]
        for i in env.map:
            map += list(i)
        for s in range(env.nS):
            letter = map[s]
            if letter in b'GH': #이렇게 해도 되는건가
                continue
            else:
                for a in range(env.nA):
                    for i in env.MDP[s][a]:
                        V[s] += policy[s][a] * (i[2] + gamma * (v[i[1]])) *i[0]

        delta = min(delta , np.sum(np.abs(v-V)))

    return V

def policy_improvement(env, V, gamma=0.99):
    policy = np.zeros([env.nS, env.nA]) / env.nA

    for s in range(env.nS):
        q = []
        for a in range(env.nA):
            policy[s][a] = 0
            q.append(0)
            for i in env.MDP[s][a]:
                q[-1] += i[2] + gamma*V[i[1]]
        policy[s][np.argmax(q)] = 1

    return policy


def policy_iteration(env, gamma=0.99, theta=1e-8):
    V = np.zeros(env.nS)
    policy = np.ones([env.nS, env.nA]) / env.nA
    policy1 = np.zeros([env.nS, env.nA]) / env.nA
    delta= np.float64(1.0)

    while (policy!=policy1).all():
        policy1 = policy.copy()
        V = policy_evaluation(env,policy,gamma,theta)
        policy = policy_improvement(env,V,gamma)

    V = policy_evaluation(env,policy,gamma,theta)
    return policy, V

def value_iteration(env, gamma=0.99, theta=1e-8):
    V = np.zeros(env.nS)
    policy = np.ones([env.nS, env.nA]) / env.nA

    delta = np.float64(1.0)
    while (delta > theta):
        v = V.copy()
        V = np.zeros(env.nS)
        map = []
        for i in env.map:
            map += list(i)
        for s in range(env.nS):
            letter = map[s]
            if letter in b'GH':  # 이렇게 해도 되는건가
                continue
            else:
                li=[]
                for a in range(env.nA):
                    li.append(0)
                    for i in env.MDP[s][a]:
                        li[-1] +=  (i[2] + gamma * (v[i[1]])) * i[0]
                V[s]=max(li)

        delta = min(delta, np.sum(np.abs(v - V)))
    for s in range(env.nS):
        q = []
        for a in range(env.nA):
            policy[s][a] = 0
            q.append(0)
            for i in env.MDP[s][a]:
                q[-1] += i[2] + gamma * V[i[1]]
        policy[s][np.argmax(q)] = 1


    return policy, V

