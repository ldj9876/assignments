import numpy as np

def find_nearest(pt, set):
    dist = np.linalg.norm(pt-set[0],axis=1)
    result = np.zeros([pt.shape[0],2],dtype=np.float)
    result[:,0] = np.copy(dist)

    for i in range(1,set.shape[0]):
        dist = np.linalg.norm(pt-set[i],axis=1)
        B = result[:,0] > dist
        result[:,1] = result[:,1] + B * (i - result[:,1])
        result[:,0] = result[:,0] + B * (dist - result[:,0])
    return np.copy(result[:,1])

def update_center(features, near_pt,center):
    result = np.copy(center)
    n = np.ones([1000,1])
    for i in range(features.shape[0]):
        result[int(near_pt[i])] += features[i]
        n[int(near_pt[i])][0] += 1
    result = result / n
    return result

def k_means(features, center, step, th=0.01):
    for i in range(step):
        nearest = find_nearest(features,center)
        center2 = update_center(features, nearest, center)
        if np.linalg.norm(center-center2) <th:
            break
        center = np.copy(center2)
    return center

def cal_histogram(sift, centers):
    nearest = find_nearest(sift,centers)
    histogram = np.zeros([1000])
    for i in range(nearest.shape[0]):
        histogram[int(nearest[i])] +=1
    return histogram


