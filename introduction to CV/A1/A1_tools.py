import cv2, time
import numpy as np

def cross_correlation_2d(img, kernel):
    img_h, img_w = img.shape
    ker_h, ker_w = kernel.shape
    h, w = ker_h // 2, ker_w // 2

    pad = np.zeros([img_h + ker_h - 1, img_w + ker_w - 1])
    pad[h:h + img_h, w:w + img_w] = img

    result = np.zeros([img_h, img_w])
    pad[:h, :w] = img[0][0]
    pad[:h, w + img_w:] = img[0][img_w - 1]
    pad[h + img_h:, :w] = img[img_h - 1][0]
    pad[h + img_h:, w + img_w:] = img[img_h - 1][img_w - 1]

    pad[:h, w:w + img_w] = img[:1, 0:img_w]
    pad[h + img_h:, w:w + img_w] = img[img_h - 1:, 0:img_w]

    pad[h:h + img_h, :w] = img[0:img_h, :1]
    pad[h:h + img_h, w + img_w:] = img[0:img_h, img_w - 1:]

    for i in range(img_h):
        for j in range(img_w):
            result[i][j] = np.sum(pad[i:i + ker_h, j:j + ker_w] * kernel)
    return result


def cross_correlation_1d(img, kernel):
    img_h, img_w = img.shape
    ker_h, ker_w = kernel.shape

    if ker_h == 1:
        w = ker_w // 2
        pad = np.zeros([img_h, img_w + ker_w - 1])
        pad[:img_h, w:w + img_w] = img

        result = np.zeros([img_h, img_w])

        pad[:img_h, :w] = img[0:img_h, :1]
        pad[:img_h, w + img_w:] = img[0:img_h, img_w - 1:]

        for i in range(img_h):
            for j in range(img_w):
                result[i][j] = np.sum(pad[i:i + 1, j:j + ker_w] * kernel)

    else:  # ker_w==1
        h = ker_h // 2
        pad = np.zeros([img_h + ker_h - 1, img_w])
        pad[h:h + img_h, :img_w] = img

        result = np.zeros([img_h, img_w])

        pad[:h, :img_w] = img[:1, 0:img_w]
        pad[h + img_h:, :img_w] = img[img_h - 1:, 0:img_w]
        for i in range(img_h):
            for j in range(img_w):
                result[i][j] = np.sum(pad[i:i + ker_h, j:j + 1] * kernel)

    return result


def get_gaussian_filter_1d(size, sigma): #horizontal
    ker_ver = np.zeros([1,size])
    for j in range(size):
        x = j - int(size/2)
        ker_ver[0][j]= np.exp(-(x**2)/(2*sigma*sigma)) /(sigma * np.sqrt(2*np.pi))
    ker_ver=ker_ver/np.sum(ker_ver)
    return ker_ver


def get_gaussian_filter_2d(size, sigma): #합으로 나눠서 정규화?
    ker = np.zeros([size, size])
    var = sigma * sigma
    for j in range(size):
        for i in range(size):
            x = j - int(size / 2)
            y = i - int(size / 2)
            ker[j][i] = (np.exp(-((x ** 2) + (y ** 2)) / (2 * var))) / (2 * np.pi * var)
    ker= ker/np.sum(ker)
    return ker


def sobelfilter(img):
    sx = np.array([[-1, 0, 1], [-2, 0, 2], [-1, 0, 1]])
    sy = np.transpose(sx)

    fx = cross_correlation_2d(img, sx)
    fy = cross_correlation_2d(img, sy)
    return fx, fy


def compute_image_gradient(img):  # dir 단위가 degree 아니면 radian?
    sobel_x = np.array([[-1, 0, 1], [-2, 0, 2], [-1, 0, 1]])
    sobel_y = np.transpose(sobel_x)
    fx = cross_correlation_2d(img, sobel_x)
    fy = cross_correlation_2d(img, sobel_y)
    mag = np.sqrt(fx * fx + fy * fy)
    dir = np.arctan2(fy,fx)
    return mag, dir


def non_maximum_suppression_dir(mag,dir):
    pi = np.pi
    dir = ((dir+ pi/8)//(pi/4))
    dir = dir.astype('int32')
    di=[[0,1],[1,1],[1,0],[-1,0],[1,-1]]
    img_h, img_w= mag.shape
    mag2=np.zeros([img_h,img_w])

    pad = np.zeros([img_h+2,img_w+2])
    pad[1:img_h+1,1:img_w+1]=mag

    for i in range(img_h):
        for j in range(img_w):
            if pad[i+1][j+1] >= pad[i+di[dir[i][j]][0]+1][j+di[dir[i][j]][1]+1] and pad[i+1][j+1] >= pad[i-di[dir[i][j]][0]+1][j-di[dir[i][j]][1]+1]:
                mag2[i][j]=mag[i][j]
    return mag2


def compute_corner_response(img):
    fx, fy = sobelfilter(img)
    fx = fx
    fy = fy
    img_h, img_w = img.shape

    IxIx = fx * fx
    IxIy = fx * fy
    IyIy = fy * fy

    R = np.zeros([img_h, img_w])
    w = np.ones([5, 5])

    a = cross_correlation_2d(IxIx, w)  # M=(a b)
    b = cross_correlation_2d(IxIy, w)  # (b d)
    d = cross_correlation_2d(IyIy, w)

    R = (a * d - b * b) - 0.04 * ((a + d) ** 2)
    R = abs(R * (R > 0))
    R = R / np.max(R)

    return R


def non_maximum_suppression_win(R, winSize):
    R_h,R_w=R.shape
    s = winSize//2
    result=np.copy(R)

    pad = np.zeros([R_h + winSize - 1, R_w + winSize - 1])
    pad[s:s + R_h, s:s + R_w] = R

    pad=np.zeros([R_h+s,R_w+s])
    pad[s:R_h+s,s:R_w+s]=R

    for i in range(R_h):
        for j in range(R_w):
            w = pad[i:i+winSize,j:j+winSize]
            if R[i][j]!=np.max(w):
                result[i][j]=0
    return result