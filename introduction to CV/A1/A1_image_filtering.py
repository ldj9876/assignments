import cv2, time
import numpy as np
import time
from A1_tools import *

gaussian_1d_hor = get_gaussian_filter_1d(5,1)
gaussian_2d = get_gaussian_filter_2d(5,1)
gaussian_1d_ver = np.transpose(gaussian_1d_hor)

print("gaussian filter 1D (5,1)\n",gaussian_1d_hor)
print("gaussian filter 2D (5,1)\n",gaussian_2d)

IMAGE_FILE_PATH = "./lenna.png"
img = cv2.imread(IMAGE_FILE_PATH, cv2.IMREAD_GRAYSCALE)
shp = cv2.imread("./shapes.png", cv2.IMREAD_GRAYSCALE)
img_h, img_w = img.shape
shp_h, shp_w = shp.shape
#1-2


gau_ker=[]
img_1 = np.zeros([img_h, img_w])
shp_1 = np.zeros([shp_h, shp_w])

result_img = np.zeros([img_h*3,img_w*3])
result_shp = np.zeros([shp_h*3,shp_w*3])
st=[['5x5','11x11','17x17'],[' s=1',' s=6',' s=11']]


for i in range(3):
    for j in range(3):
        ker = get_gaussian_filter_2d(6*i+5,5*j+1)
        img_1 = cross_correlation_2d(img,ker)
        shp_1 = cross_correlation_2d(shp,ker)
        result_img[i * img_h: (i + 1) * img_h,j * img_w: (j + 1) * img_w] = img_1
        result_shp[i * shp_h: (i + 1) * shp_h,j * shp_w: (j + 1) * shp_w] = shp_1

        cv2.putText(result_img, st[0][i] + st[1][j], (j * img_w, i * img_h + 25), cv2.FONT_HERSHEY_COMPLEX, 1,
                    (255, 255, 255))
        cv2.putText(result_shp, st[0][i] + st[1][j], (j * shp_w, i * shp_h + 25), cv2.FONT_HERSHEY_COMPLEX, 1,
                    (0, 0, 0))
cv2.imwrite('./result/part_1_gaussian_filtered_lenna.png',result_img)
cv2.imwrite('./result/part_1_gaussian_filtered_shapes.png',result_shp)


start_1d = time.time()
img_gau_1d = cross_correlation_1d(img, gaussian_1d_ver)
img_gau_1d = cross_correlation_1d(img_gau_1d, gaussian_1d_hor)
end_1d = time.time()
img_gau_2d = cross_correlation_2d(img, gaussian_2d)
end_2d = time.time()
dif_img = np.abs(img_gau_1d - img_gau_2d)
sums_img = np.sum(dif_img)
print("lenna 1d(5,1) twice time:",end_1d - start_1d)
print("lenna 2d(5,1) time:",end_2d - end_1d)
print("lenna sum of difference:",sums_img)

start_1d = time.time()
shp_gau_1d = cross_correlation_1d(shp, gaussian_1d_ver)
shp_gau_1d = cross_correlation_1d(shp_gau_1d, gaussian_1d_hor)
end_1d = time.time()
shp_gau_2d = cross_correlation_2d(shp, gaussian_2d)
end_2d = time.time()
dif_shp = np.abs(shp_gau_1d - shp_gau_2d)
sums_shp = np.sum(dif_shp)
print("shapes gaussian 1d filter twice time:",end_1d - start_1d)
print("shapes gaussian 2d filter time:",end_2d - end_1d)
print("shapes sum of difference:",sums_shp)



cv2.imshow("gaussian_lenna",result_img.astype("uint8"))
cv2.imshow("difference_lenna", dif_img.astype("uint8"))
cv2.waitKey(0)
cv2.destroyAllWindows()


cv2.imshow("gaussian_shapes",result_shp.astype("uint8"))
cv2.imshow("difference_shapes", dif_shp.astype("uint8"))
cv2.waitKey(0)
cv2.destroyAllWindows()