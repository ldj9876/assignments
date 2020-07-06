import numpy as np
import cv2
import random



def get_transformed_image(img, M):
    img_h, img_w = img.shape
    result = np.ones([801,801])*255
    inv_M = np.linalg.inv(M)

    pos = np.ones((3,img_h,img_w))
    px = np.ones((img_h,img_w))
    py = np.ones((img_h,img_w))

    for i in range(img_h):
        pos[0][i] = np.arange(img_w) -400
        pos[1][i] = 400-i
    pos=pos.reshape(3,img_h*img_w)
    pos_t = np.dot(inv_M,pos)
    pos_t=pos_t.reshape(3,img_h,img_w)
    id = np.zeros((2,img_h,img_w))
    id[0] = 400 - pos_t[1]
    id[1] = 400 + pos_t[0]
    idx2 = np.array([id[0],id[1]])

    idx = idx2.clip(0,800)
    idx = idx.transpose()
    a=0
    for j,a in zip(idx,list(range(img_w))):
        for i, b in zip(j,list(range(img_h))):
            result[b][a] = img[int(i[0])][int(i[1])]
    return result

deg = np.deg2rad(5)
mat=dict()
mat['a'] = np.array([[1,0,-5],[0,1,0],[0,0,1]])
mat['d'] = np.array([[1,0,5],[0,1,0],[0,0,1]])
mat['w'] = np.array([[1,0,0],[0,1,5],[0,0,1]])
mat['s'] = np.array([[1,0,0],[0,1,-5],[0,0,1]])
mat['r'] = np.array([[np.cos(deg),-np.sin(deg),0],[np.sin(deg),np.cos(deg),0],[0,0,1]])
mat['R'] = np.array([[np.cos(-deg),-np.sin(-deg),0],[np.sin(-deg),np.cos(-deg),0],[0,0,1]])
mat['f'] = np.array([[-1,0,0],[0,1,0],[0,0,1]])
mat['F'] = np.array([[1,0,0],[0,-1,0],[0,0,1]])
mat['x'] = np.array([[0.95,0,0],[0,1,0],[0,0,1]])
mat['X'] = np.array([[1.05,0,0],[0,1,0],[0,0,1]])
mat['y'] = np.array([[1,0,0],[0,0.95,0],[0,0,1]])
mat['Y'] = np.array([[1,0,0],[0,1.05,0],[0,0,1]])

