import cv2, time
import numpy as np
from A1_tools import *

gaussian = get_gaussian_filter_2d(7,1.5)
img = cv2.imread("./lenna.png", cv2.IMREAD_GRAYSCALE)
shp = cv2.imread("./shapes.png", cv2.IMREAD_GRAYSCALE)
img = cross_correlation_2d(img, gaussian)
shp = cross_correlation_2d(shp, gaussian)

time1 = time.time()
img_mag, img_dir = compute_image_gradient(img)
time2 = time.time()
shp_mag, shp_dir = compute_image_gradient(shp)
time3 = time.time()
img_suppressed_mag = non_maximum_suppression_dir(img_mag, img_dir)
time4 = time.time()
shp_suppressed_mag = non_maximum_suppression_dir(shp_mag, shp_dir)
time5 = time.time()

print("gradient lenna time:",time2-time1)
print("NMS img time:",time4-time3)
print("gradient shapes time:",time3-time2)
print("NMS shapes time:",time5-time4)

img_mag = 255*img_mag/np.max(img_mag)
shp_mag = 255*shp_mag/np.max(shp_mag)
img_suppressed_mag = 255*img_suppressed_mag/np.max(img_suppressed_mag)
shp_suppressed_mag = 255*shp_suppressed_mag/np.max(shp_suppressed_mag)

cv2.imwrite("./result/part_2_edge_raw_lenna.png", img_mag )
cv2.imwrite("./result/part_2_edge_raw_shapes.png",shp_mag )

cv2.imwrite("./result/part_2_edge_sup_lenna.png", img_suppressed_mag )
cv2.imwrite("./result/part_2_edge_sup_shapes.png",shp_suppressed_mag )

cv2.imshow("lenna_edge_raw",img_mag.astype("uint8")  )
cv2.imshow("lenna_edhe_sup",img_suppressed_mag.astype("uint8"))
cv2.waitKey(0)
cv2.destroyAllWindows()

cv2.imshow("shapes_edge_raw",shp_mag.astype("uint8")  )
cv2.imshow("shapes_edge_sup",shp_suppressed_mag.astype("uint8") )
cv2.waitKey(0)
cv2.destroyAllWindows()

