import numpy as np
import cv2
from compute_avg_reproj_error import compute_avg_reproj_error
import random

def compute_F_raw(M):
    m = M.shape[0]
    x1, y1, x2, y2 = M[:,0], M[:,1], M[:,2], M[:,3]
    matrix = np.ones([m,9])

    matrix[:, 0] = x1*x2
    matrix[:, 1] = x1*y2
    matrix[:, 2] = x1
    matrix[:, 3] = y1*x2
    matrix[:, 4] = y1*y1
    matrix[:, 5] = y1
    matrix[:, 6] = x2
    matrix[:, 7] = y2
    u, s, vt = np.linalg.svd(matrix)
    return vt[-1].reshape((3,3)) #/vt[-1][-1] #이거 해야되는건가?)

def compute_F_norm(M):
    mean = np.average(M,axis=0)
    x1, y1, x2, y2 = M[:, 0], M[:, 1], M[:, 2], M[:, 3]

    M2 = M - mean
    A1 = np.array([[1,0,-mean[0]],[0,1,-mean[1]],[0,0,1]])
    A2 = np.array([[1, 0, -mean[2]], [0, 1, -mean[3]], [0, 0, 1]])

    maxi = np.max(np.abs(M2))
    T1 = np.array([[1/maxi,0,0],[0,1/maxi,0],[0,0,1]])
    T2 = np.array([[1 / maxi, 0, 0], [0, 1 / maxi, 0], [0, 0, 1]])
    M2 = M2 / maxi


    F = compute_F_raw(M2)

    u,s,vt = np.linalg.svd(F)
    s[2]=0
    F = np.dot(u,np.dot(np.diag(s),vt))

    X1 = np.dot(T1,A1)
    X2 = np.dot(T2,A2)
    F = np.dot(X2.transpose(), np.dot(F,X1))

    return F

def compute_F_mine(M, n=2000):
    sample = []
    error = 10000000
    N = M.shape[0]
    count = 0
    for i in range(n):
        samples = random.sample(range(N),8)
        samples.sort()
        if samples in sample:
            continue

        sample.append(samples)
        sample_M = M[samples,:]
        F = compute_F_norm(sample_M)
        e = compute_avg_reproj_error(M,F)
        if e < error:
            error = e
            result_F = F



    return result_F

def draw_line(img, line, i):
    r, w = img.shape[0], img.shape[1]
    a, b, c = line
    x = (0, int(-c/b))
    x2 = (w, int(-(a*w+c)/b))
    _, start, end = cv2.clipLine((0,0,w-1,r-1), x, x2)

    return cv2.line(img,start,end,color[i])

M = np.loadtxt('temple_matches.txt')
print("Average Reprojection Errors (temple1.png and temple2.png)")
F = compute_F_raw(M)
e = compute_avg_reproj_error(M,F)
print("   Raw =",e)
F2 = compute_F_norm(M)
e2 = compute_avg_reproj_error(M,F2)
print("   Norm =",e2)
F3_1 = compute_F_mine(M)
e3 = compute_avg_reproj_error(M,F3_1)
print("   Mine =",e3)
print()

M = np.loadtxt('house_matches.txt')
print("Average Reprojection Errors (house1.jpg and house2.jpg)")
F = compute_F_raw(M)
e = compute_avg_reproj_error(M,F)
print("   Raw =",e)
F2 = compute_F_norm(M)
e2 = compute_avg_reproj_error(M,F2)
print("   Norm =",e2)
F3_2 = compute_F_mine(M)
e3 = compute_avg_reproj_error(M,F3_2)
print("   Mine =",e3)
print()

M = np.loadtxt('library_matches.txt')
print("Average Reprojection Errors (library1.jpg and library2.jpg")
F = compute_F_raw(M)
e = compute_avg_reproj_error(M,F)
print("   Raw =",e)
F2 = compute_F_norm(M)
e2 = compute_avg_reproj_error(M,F2)
print("   Norm =",e2)
F3_3 = compute_F_mine(M)
e3 = compute_avg_reproj_error(M,F3_3)
print("   Mine =",e3)


######### 1-2
color = [(0,0,255),(0,255,0),(255,0,0)]

F3 = F3_1
img_s = cv2.imread("temple1.png")
img2_s = cv2.imread("temple2.png")

M = np.loadtxt('temple_matches.txt')
N = M.shape[0]
d = 0
while(chr(d)!='q'):
    img = np.copy(img_s)
    img2 = np.copy(img2_s)

    samples = random.sample(range(N), 3)
    sample_M = M[samples, :]
    for i in range(3):
        x = np.array([[sample_M[i, 0]], [sample_M[i, 1]], [1]])
        x2 = np.array([[sample_M[i, 2]], [sample_M[i, 3]], [1]])

        l = np.dot(F3.transpose(), x2)
        l2 = np.dot(F3, x)

        img = cv2.circle(img, (int(sample_M[i, 0]), int(sample_M[i, 1])), 5, color[i], 2)
        img2 = cv2.circle(img2, (int(sample_M[i, 2]), int(sample_M[i, 3])), 5, color[i], 2)

        img = draw_line(img, l, i)
        img2 = draw_line(img2, l2, i)

    cv2.imshow("temple", cv2.hconcat([img, img2]))
    d=cv2.waitKey(0)
    cv2.destroyAllWindows()

F3 = F3_2
img_s = cv2.imread("house1.jpg")
img2_s = cv2.imread("house2.jpg")

M = np.loadtxt('house_matches.txt')
N = M.shape[0]
d = 0
while(chr(d)!='q'):
    img = np.copy(img_s)
    img2 = np.copy(img2_s)

    samples = random.sample(range(N), 3)
    sample_M = M[samples, :]
    for i in range(3):
        x = np.array([[sample_M[i, 0]], [sample_M[i, 1]], [1]])
        x2 = np.array([[sample_M[i, 2]], [sample_M[i, 3]], [1]])

        l = np.dot(F3.transpose(), x2)
        l2 = np.dot(F3, x)

        img = cv2.circle(img, (int(sample_M[i, 0]), int(sample_M[i, 1])), 5, color[i], 2)
        img2 = cv2.circle(img2, (int(sample_M[i, 2]), int(sample_M[i, 3])), 5, color[i], 2)

        img = draw_line(img, l, i)
        img2 = draw_line(img2, l2, i)

    cv2.imshow("house", cv2.hconcat([img, img2]))
    d=cv2.waitKey(0)
    cv2.destroyAllWindows()


F3 = F3_3
img_s = cv2.imread("library1.jpg")
img2_s = cv2.imread("library2.jpg")

M = np.loadtxt('library_matches.txt')
N = M.shape[0]
d = 0

while(chr(d)!='q'):
    img = np.copy(img_s)
    img2 = np.copy(img2_s)

    samples = random.sample(range(N), 3)
    sample_M = M[samples, :]
    for i in range(3):
        x = np.array([[sample_M[i, 0]], [sample_M[i, 1]], [1]])
        x2 = np.array([[sample_M[i, 2]], [sample_M[i, 3]], [1]])

        l = np.dot(F3.transpose(), x2)
        l2 = np.dot(F3, x)

        img = cv2.circle(img, (int(sample_M[i, 0]), int(sample_M[i, 1])), 5, color[i], 2)
        img2 = cv2.circle(img2, (int(sample_M[i, 2]), int(sample_M[i, 3])), 5, color[i], 2)

        img = draw_line(img, l, i)
        img2 = draw_line(img2, l2, i)

    cv2.imshow("library", cv2.hconcat([img, img2]))
    d=cv2.waitKey(0)
    cv2.destroyAllWindows()