import numpy as np
import cv2
from tools import *

smile = cv2.imread("smile.png", cv2.IMREAD_GRAYSCALE)
s_h, s_w = smile.shape

img = np.ones([801,801])*255
img[400 - s_h//2 : 400 + s_h//2 +1, 400 - s_w//2 : 400 + s_w//2 +1] = smile

img2 = np.copy(img)


cv2.arrowedLine(img2,(400,801),(400,0),(0,0,0),thickness=2,tipLength=0.03)
cv2.arrowedLine(img2,(0,400),(801,400),(0,0,0),thickness=2,tipLength=0.03)

cv2.imshow("smile",img2)
M = np.identity(3)
while True: # 행렬 곱한걸 저장해서 변환?
    key = chr(cv2.waitKey(0))
    if key in 'adwsrRfFxXyY':
        M = np.dot(mat[key],M)
        img2 = get_transformed_image(img,M)
        cv2.arrowedLine(img2, (400, 801), (400, 0), (0, 0, 0), thickness=2, tipLength=0.03)
        cv2.arrowedLine(img2, (0, 400), (801, 400), (0, 0, 0), thickness=2, tipLength=0.03)
        cv2.destroyAllWindows()
        cv2.imshow("smile",img2)
    elif key == 'H':
        M = np.identity(3)
        img2 = np.copy(img)
        cv2.arrowedLine(img2, (400, 801), (400, 0), (0, 0, 0), thickness=2, tipLength=0.03)
        cv2.arrowedLine(img2, (0, 400), (801, 400), (0, 0, 0), thickness=2, tipLength=0.03)
        cv2.destroyAllWindows()
        cv2.imshow("smile", img2)
    elif key == 'Q':
        cv2.destroyAllWindows()
        break
