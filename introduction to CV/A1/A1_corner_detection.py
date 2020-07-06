import cv2, time
import numpy as np
from A1_tools import *


gaussian = get_gaussian_filter_2d(7,1.5)
img = cv2.imread("./lenna.png", cv2.IMREAD_GRAYSCALE)
shp = cv2.imread("./shapes.png", cv2.IMREAD_GRAYSCALE)


img_h, img_w = img.shape
shp_h, shp_w = shp.shape

img = cross_correlation_2d(img, gaussian)
shp = cross_correlation_2d(shp, gaussian)

time1=time.time()
R_img = compute_corner_response(img)
time2=time.time()
R_shp = compute_corner_response(shp)
time3=time.time()
suppressed_R_img = non_maximum_suppression_win(R_img, 11)
time4=time.time()
suppressed_R_shp = non_maximum_suppression_win(R_shp, 11)
time5=time.time()

print("lenna corner response time: ",time2-time1)
print("lenna NMS time: ",time4-time3)
print("shapes corner response time: ",time3-time2)
print("shapes NMS time: ",time5-time4)

R_img_2=cv2.cvtColor(img.astype('uint8'),cv2.COLOR_GRAY2BGR)
R_shp_2=cv2.cvtColor(shp.astype('uint8'),cv2.COLOR_GRAY2BGR)

for i in range(img_h):
    for j in range(img_w):
        if R_img[i][j]>0.1:
            R_img_2=cv2.line(R_img_2,(j,i),(j,i),(0,255,0))

for i in range(shp_h):
    for j in range(shp_w):
        if R_shp[i][j]>0.1:
            R_shp_2=cv2.line(R_shp_2,(j,i),(j,i),(0,255,0))

suppressed_R_img_2 = cv2.cvtColor(img.astype('uint8'),cv2.COLOR_GRAY2BGR)
suppressed_R_shp_2 = cv2.cvtColor(shp.astype('uint8'),cv2.COLOR_GRAY2BGR)

for i in range(img_h):
    for j in range(img_w):
        if suppressed_R_img[i][j]>0.1:
            cv2.circle(suppressed_R_img_2,(j,i),3,(0,255,0))

for i in range(shp_h):
    for j in range(shp_w):
        if suppressed_R_shp[i][j]>0.1:
            cv2.circle(suppressed_R_shp_2,(j,i),3,(0,255,0))



cv2.imwrite("./result/part_3_corner_raw_lenna.png", R_img*255)
cv2.imwrite("./result/part_3_corner_raw_shapes.png", R_shp*255)

cv2.imwrite("./result/part_3_corner_bin_lenna.png", R_img_2)
cv2.imwrite("./result/part_3_corner_bin_shapes.png", R_shp_2)

cv2.imwrite("./result/part_3_corner_sup_lenna.png", suppressed_R_img_2)
cv2.imwrite("./result/part_3_corner_sup_shapes.png", suppressed_R_shp_2)

cv2.imshow("lenna corner raw",R_img)
cv2.imshow("lenna corner bin",R_img_2)
cv2.imshow("lenna corner sup:",suppressed_R_img_2)
cv2.waitKey(0)
cv2.destroyAllWindows()


cv2.imshow("shapes corner raw",R_shp)
cv2.imshow("shapes corner bin",R_shp_2)
cv2.imshow("shapes corner sup:",suppressed_R_shp_2)
cv2.waitKey(0)
cv2.destroyAllWindows()
print(np.max(R_img), np.max(R_shp))
