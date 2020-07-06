import sys
import numpy as np
import tensorflow.compat.v1 as tf
import random
import gym
from collections import deque
keras = tf.keras

DISCOUNT_RATE = 0.99            # gamma parameter
REPLAY_MEMORY = 50000           # Replay buffer 의 최대 크기
LEARNING_RATE = 0.001           # learning rate parameter
LEARNING_STARTS = 1000          # 1000 스텝 이후 training 시작
tf.disable_eager_execution()

class DQN:
    def __init__(self, env, double_q=False, multistep=False, per=False):


        self.env = env
        self.state_size = self.env.observation_space.shape[0]
        self.action_size = self.env.action_space.n

        self.double_q = double_q    # Double DQN        구현 시 True로 설정, 미구현 시 False
        self.per = per              # PER               구현 시 True로 설정, 미구현 시 False
        self.multistep = multistep  # Multistep(n-step) 구현 시 True로 설정, 미구현 시 False
        self.n_steps = 4            # Multistep(n-step) 구현 시의 n 값

        self.epsilon = 1
        self.model = self._build_network()
        if self.double_q:
            self.eval = self._build_network()


    def _build_network(self):
        model = keras.models.Sequential()
        dense = keras.layers.Dense
        model.add(dense(512, input_dim=self.state_size, activation="relu"))
        model.add(dense(self.action_size))
        optimizer = keras.optimizers.Adam(lr=LEARNING_RATE)
        model.compile(loss="mean_squared_error", optimizer=optimizer)
        return model



    def predict(self, state):
        # state를 넣어 policy에 따라 action을 반환
        state = state.reshape(-1,self.state_size)
        action = np.argmax(self.model.predict(state)[0])
        return action


    def train_minibatch(self, history, batch_size=512):
        minibatch = random.sample(history,batch_size)
        state = np.vstack([m[0] for m in minibatch])
        action = np.array([m[1] for m in minibatch])
        reward = np.array([m[2] for m in minibatch])
        next_state = np.vstack([m[3] for m in minibatch])
        done = np.array([m[-1] for m in minibatch])
        done = 1-done
        Q = self.model.predict(state)

        if self.double_q:
            Q[np.arange(len(state)), action] = reward + done * DISCOUNT_RATE * np.max(self.eval.predict(next_state),
                                                                                      axis=1)
        elif self.multistep:
            rewards=[reward]
            next_state = np.vstack([m[-3] for m in minibatch])
            for i in range(self.n_steps-1):
                rewards.append(np.array([m[7+5*i] for m in minibatch]))
            Q[np.arange(len(state)), action] = rewards[0]
            for i in range(1, self.n_steps):
                Q[np.arange(len(state)),action] += rewards[i] * (DISCOUNT_RATE**(i))
            Q[np.arange(len(state)),action] += done * (DISCOUNT_RATE**self.n_steps) * np.max(self.model.predict(next_state), axis=1)
        else:
            Q[np.arange(len(state)), action] = reward + done * DISCOUNT_RATE * np.max(self.model.predict(next_state),
                                                                                      axis=1)
        self.model.fit(state, Q, epochs=1,verbose=0)

    def update_epsilon(self, epsilon) :
        return np.clip(epsilon*0.995,0.01,1)

    def update_eval(self):
        self.eval.set_weights(self.model.get_weights())





    # episode 최대 회수는 구현하는 동안 더 적게, 더 많이 돌려보아도 무방합니다.
    # 그러나 제출 시에는 최종 결과 그래프를 그릴 때는 episode 최대 회수를
    # 1000 번으로 고정해주세요. (다른 학생들과의 성능/결과 비교를 위해)
    def learn(self, max_episode:int = 1000):

        episode_record = []     # plot을 그리기 위해 데이터를 저장하는 list
        last_100_game_reward = deque(maxlen=100)
        history = deque(maxlen=REPLAY_MEMORY)

        print("=" * 70)
        print("Double : {}    Multistep : {}/{}    PER : {}".format(self.double_q, self.multistep, self.n_steps, self.per))
        print("=" * 70)
        eps = self.epsilon
        if self.double_q:
            self.update_eval()
        for episode in range(max_episode):
            done = False
            state = self.env.reset()
            step_count = 0

            # episode 시작
            while not done:
                if random.random() > eps:
                    action = self.predict(state)
                else:
                    action = self.env.action_space.sample()

                if done:
                    reward = -1

                next_state, reward, done, _ = self.env.step(action)
                state = np.reshape(state,(-1,self.state_size))
                next_state = np.reshape(next_state,(-1,self.state_size))
                history.append([state, action, reward, next_state, done])
                if self.multistep:
                    for i in range(self.n_steps-1):
                        history[-1] = history[-1] + [state, action, 0, next_state, 1]
                    history[-1].append(0)
                    for i in range(self.n_steps-1):
                        if len(history) < i + 1:
                            break
                        if history[-(i+1)][-(2+i*5)]==0:
                            history[-(i + 1)][-(6+i*5):-(1+i*5)] = [state, action, reward, next_state, done]
                if len(history) > LEARNING_STARTS: #이거로 해야되나?
                    self.train_minibatch(history)

                state = next_state
                step_count += 1
                eps= self.update_epsilon(eps)
            if self.double_q:
                self.update_eval()
            self.epsilon = np.clip(self.epsilon*0.99,0.01,1)
            eps = self.epsilon
            # 최근 100개의 에피소드 reward 평균을 저장
            last_100_game_reward.append(-step_count)
            avg_reward = np.mean(last_100_game_reward)
            episode_record.append(avg_reward)
            print("[Episode {:>5}]  episode steps: {:>5} avg: {}".format(episode, step_count, avg_reward))

        return episode_record
