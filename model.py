import numpy as np
import sys
from PIL import Image

def relu(x):
    return ( x > 0) * x

def predict(path):
    img = Image.open(path)

    w_0_1 = np.loadtxt("Project\Digit Recognition\weights_0_1.txt")
    w_1_2 = np.loadtxt("Project\Digit Recognition\weights_1_2.txt")

    layer_0 = np.asarray(img, dtype="float64").reshape(1, 28 * 28) / 255
    layer_1 = relu(layer_0.dot(w_0_1))
    layer_2 = layer_1.dot(w_1_2)

    print(np.argmax(layer_2))

if __name__ == "__main__":
    # first argument is image path passed when module is called
    image_path = sys.argv[1]
    predict(image_path)

### Image relative path
# Project\Digit Recognition\data\testSet\testSet\img_8.jpg

### Module/Script relative path
# "Project\Digit Recognition\model.py"

### Sample script to run in cmd
### with root set to D:\OneDrive - nhg.vn\Self-study\Deep Learning
# python "Project\Digit Recognition\model.py" "Project\Digit Recognition\data\testSet\testSet\img_37.jpg"