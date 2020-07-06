import numpy as np
from tools import *
import struct
dir_s= "./sift/sift10"

N = 1000
D = 128
first = np.array([N,D],dtype=np.int32)

sifts = np.ndarray([N],dtype=np.object)
sifts2 = np.ndarray([N],dtype=np.object)

for i in range(1000):
     sifts[i] = np.fromfile(dir_s + str(i).zfill(4), dtype=np.uint8).astype(np.int16).reshape(-1, 128)
     sifts2[i] = sifts[i][:50]


features = np.vstack(sifts2).astype(np.float32)

n_cluster = 1000
centers = np.random.choice(50000, n_cluster,replace=False)
centers = features[centers]
center = k_means(features,centers,500)

descriptor = np.ndarray([sifts2.shape[0],n_cluster])

for i in range(sifts2.shape[0]):
     descriptor[i] = cal_histogram(sifts2[i],center)

file = open("A3_2017312576.des", 'wb')
file.write(struct.pack('ii', 1000, 1000))

for i in range(1000):
    for j in range(1000):
        file.write(struct.pack('d', descriptor[i][j]))
file.close()