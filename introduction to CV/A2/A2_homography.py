import numpy as np
import cv2, random, time

desk = cv2.imread("cv_desk.png",cv2.IMREAD_GRAYSCALE)
cover = cv2.imread("cv_cover.jpg",cv2.IMREAD_GRAYSCALE)

orb = cv2.ORB_create()
kp = orb.detect(desk,None)
kp, des = orb.compute(desk, kp)
orb2 = cv2.ORB_create()
kp_c = orb2.detect(cover,None)
kp_c, des_c = orb2.compute(cover,kp_c)

match = []

for i in range(len(des)):
    dis_min = cv2.norm(des[i], des_c[0], cv2.NORM_HAMMING)
    idx = (i,0)
    for j in range(1,len(des_c)):
        dis = cv2.norm(des[i], des_c[j],cv2.NORM_HAMMING)
        if dis_min > dis:
            dis_min = dis
            idx = (i,j)

    match.append(cv2.DMatch(idx[0],idx[1],dis_min))


match = sorted(match, key = lambda x:x.distance)
res=None
res = cv2.drawMatches(desk,kp,cover,kp_c,match[:10],res,flags=2)
cv2.imshow("2-1",res)
cv2.waitKey(0)
cv2.destroyAllWindows()

def normalize(arr):
    mean = arr.mean(axis=0)
    max_norm = np.max(np.linalg.norm(arr - mean, axis=1))
    normal_arr = np.sqrt(2)*(arr-mean) / max_norm
    T = np.identity(3)
    T[0][2], T[1][2] = -mean[0], -mean[1]
    T[0:2, :] = np.sqrt(2)*T[0:2, :] / max_norm
    return T, normal_arr

def compute_homography(srcP, destP):
    Ts, xs = normalize(srcP)
    Td, xd = normalize(destP)

    Td_i = np.linalg.inv(Td)

    N = srcP.shape[0]

    A = np.zeros([2*N,9])

    for i in range(N):
        x, y = xs[i]
        x_, y_ = xd[i]
        A[2 * i:2 * i + 2, :] = np.array(
            [[-x, -y, -1, 0, 0, 0, x * x_, y * y_, x_], [0, 0, 0, -x, -y, -1, x * y_, y * y_, y_]])

    u, s, vt = np.linalg.svd(A)
    Hn = vt[-1].reshape(3,3)
    if Hn[2][2]!=0:
        Hn = Hn / Hn[2][2]
    else:
        Hn = np.zeros([3,3])

    H = Td_i.dot(Hn.dot(Ts))
    return H

def compute_homography_ransac(srcP, destP, th):
    N = srcP.shape[0]
    max_c = 0
    inliers = []
    sample=[]
    count=0
    a=time.time()
    while(len(inliers)<10 or count<1500):
        samples = random.sample(range(N),4)
        samples.sort()
        if samples in sample:
            continue
        else:
            sample.append(samples)
        count += 1
        srcP_r = np.array([srcP[samples[0]], srcP[samples[1]], srcP[samples[2]], srcP[samples[3]]])
        destP_r = np.array([destP[samples[0]], destP[samples[1]], destP[samples[2]], destP[samples[3]]])
        H_r = compute_homography(srcP_r,destP_r)
        c=0
        inliers_r=[]
        for j in range(N):
            x = np.array([[srcP[j][0]],[srcP[j][1]],[1]])
            x_ = np.array([[destP[j][0]],[destP[j][1]],[1]])
            dif = x_ - H_r.dot(x)
            diff = dif[0][0]**2 + dif[1][0]**2
            if diff < th**2 :
                c +=1
                inliers_r.append(j)
        if max_c < c:
            max_c = c
            inliers = list(inliers_r)

    srcP2 = np.zeros([max_c+1,2])
    destP2 = np.zeros([max_c+1,2])
    for i in range(len(inliers)):
        srcP2[i] = srcP[inliers[i]]
        destP2[i] = destP[inliers[i]]
    srcP2[-1] = srcP[0]
    destP2[-1] = destP[0]
    H = compute_homography(srcP2,destP2)

    return H



n=19
src = np.zeros([n,2])
dest = np.zeros([n,2])

for i in range(n):
    src[i] = kp_c[match[i].trainIdx].pt
    dest[i] = kp[match[i].queryIdx].pt

H = compute_homography(src,dest)



img = cv2.warpPerspective(cover,H,(desk.shape[1],desk.shape[0]))
desk1 = np.copy(desk)
for i in range(desk.shape[0]):
    for j in range(desk.shape[1]):
        if img[i][j]:
            desk1[i][j] = img[i][j]


cv2.imshow("with-normalization",img.astype("uint8"))
cv2.imshow("with-normalization2",desk1.astype("uint8"))
cv2.waitKey(0)
cv2.destroyAllWindows()

H2 = compute_homography_ransac(src,dest,3)

img2 = cv2.warpPerspective(cover,H2,(desk.shape[1],desk.shape[0]))
desk2 = np.copy(desk)
for i in range(desk.shape[0]):
    for j in range(desk.shape[1]):
        if img2[i][j]:
            desk2[i][j] = img2[i][j]
cv2.imshow("with-RANSAC",img2.astype("uint8"))
cv2.imshow("with-RANSAC2",desk2.astype("uint8"))
cv2.waitKey(0)
cv2.destroyAllWindows()

hp=cv2.imread("hp_cover.jpg",cv2.IMREAD_GRAYSCALE)
hp = cv2.resize(hp,(cover.shape[1],cover.shape[0]),interpolation=cv2.INTER_AREA)
img3 = cv2.warpPerspective(hp,H2,(desk.shape[1],desk.shape[0]))
desk3 = np.copy(desk)
for i in range(desk.shape[0]):
    for j in range(desk.shape[1]):
        if img3[i][j]:
            desk3[i][j] = img3[i][j]
cv2.imshow("hp-RANSAC",img3.astype("uint8"))
cv2.imshow("hp-RANSAC2",desk3.astype("uint8"))
cv2.waitKey(0)
cv2.destroyAllWindows()

left = cv2.imread("diamondhead-10.png",cv2.IMREAD_GRAYSCALE)
right = cv2.imread("diamondhead-11.png",cv2.IMREAD_GRAYSCALE)

orb = cv2.ORB_create()
kp = orb.detect(left,None)
kp, des = orb.compute(left, kp)
orb2 = cv2.ORB_create()
kp_c = orb2.detect(right,None)
kp_c, des_c = orb2.compute(right,kp_c)

match = []

for i in range(len(des)):
    dis_min = cv2.norm(des[i], des_c[0], cv2.NORM_HAMMING)
    idx = (i,0)
    for j in range(1,len(des_c)):
        dis = cv2.norm(des[i], des_c[j],cv2.NORM_HAMMING)
        if dis_min > dis:
            dis_min = dis
            idx = (i,j)
    match.append(cv2.DMatch(idx[0],idx[1],dis_min))


match = sorted(match, key = lambda x:x.distance)
res=None
res = cv2.drawMatches(left,kp,right,kp_c,match[:10],res,flags=2)

n=20
src = np.zeros([n,2])
dest = np.zeros([n,2])

for i in range(n):
    src[i] = kp_c[match[i].trainIdx].pt
    dest[i] = kp[match[i].queryIdx].pt

left_h, left_w = left.shape
H2 = compute_homography_ransac(src,dest,3)
img = cv2.warpPerspective(right,H2,(left_w+right.shape[1],left_h))
img2 = np.copy(img)

for i in range(left_w,img.shape[1]):
    tf = img[-1,i:i+20] !=0
    if np.sum(tf)==0:
        w=i
        break
img = img[:,:w]

for i in range(left_w-70, left_w):
    img[:,i] = img[:,i]*((i-left_w+70)/70) + left[:,i]*((left_w-i)/70)

img[:,:left_w-70]=left[:,:left_w-70]
img2[:,:left_w] = left[:,:left_w]

cv2.imshow("diamond",img2.astype("uint8"))
cv2.imshow("diamond-blend",img.astype("uint8"))
cv2.waitKey(0)
cv2.destroyAllWindows()