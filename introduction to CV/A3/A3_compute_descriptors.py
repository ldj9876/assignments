import numpy as np
import scipy.spatial
import struct
#문제 이해 부족과 시간 부족으로 인해 제대로 구현되지 못하였습니다.
def set_cluster_centroids(data, clusters, k=250):
    result = np.empty(shape=(k,) + data.shape[1:])
    for i in range(k):
        np.mean(data[clusters == i], axis=0, out=result[i])
    return result


def kmeans(data, k, centroids=None, steps=10, th=0.01):
    centroids = data[np.random.choice(np.arange(len(data)), k, False)]
    for i in range(steps):
        dists = scipy.spatial.distance.cdist(centroids, data, 'euclidean')
        clusters = np.argmin(dists, axis=0)

        new_centroids = set_cluster_centroids(data, clusters, k)
        if  np.sum(np.absolute(new_centroids-centroids))<th:
            break
        centroids = new_centroids
    return centroids

dir_s= "./sift/sift10"


N = 1000
D = 128
first = np.array([N,D],dtype=np.int32)

sifts = np.ndarray([N],dtype=np.object)
sifts2 = np.ndarray([N],dtype=np.object)
for i in range(1000):
     sifts2[i] = np.fromfile(dir_s + str(i).zfill(4), dtype=np.uint8).reshape(-1, 128)[:50]

features2 = np.vstack(sifts2).astype(np.float32)

k= 250
def predict(centeroids,data):
    dist = [np.sum(np.linalg.norm(data - center,axis=1)) for center in centeroids]
    center = centeroids[dist.index(min(dist))]
    return center

centroids = kmeans(features2,k=250,steps=100,th=0.001)

result = np.zeros([1000,128],dtype=np.float32)

for i in range(1000):
     result[i] = predict(centroids,sifts2[i])


file = open("A3_2017312576.des",'wb')

file.write(struct.pack('ii',1000,128))

for i in range(1000):
    for j in range(128):
        file.write(struct.pack('d',result[i][j]))
file.close()
