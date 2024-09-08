import numpy as np
import sys
from PIL import Image

import logging
logger = logging.getLogger("mylogger")

def relu(x):
    return ( x > 0) * x

def predict(int_array):
    np_array = np.asarray(int_array, dtype='int8').reshape(28, 28)
    np_array = np.bitwise_not(np_array)
    image = Image.fromarray(np_array).convert("L")

    # PC
    #w_0_1 = np.load("D:\OneDrive - nhg.vn\Self-study\Deep Learning\Project\Digit Recognition\DJango\modelapi\modelapi\\nn_weights_0_1.npy")
    #w_1_2 = np.load("D:\OneDrive - nhg.vn\Self-study\Deep Learning\Project\Digit Recognition\DJango\modelapi\modelapi\\nn_weights_1_2.npy")
    # Laptop
    w_0_1 = np.load("E:\OneDrive - nhg.vn\Self-study\Deep Learning\Project\Digit Recognition\DJango\modelapi\modelapi\\nn_weights_0_1.npy")
    w_1_2 = np.load("E:\OneDrive - nhg.vn\Self-study\Deep Learning\Project\Digit Recognition\DJango\modelapi\modelapi\\nn_weights_1_2.npy")

    layer_0 = np.asarray(image, dtype="float64").reshape(1, 28 * 28) / 255
    
    layer_1 = relu(layer_0.dot(w_0_1))
    layer_2 = layer_1.dot(w_1_2)
    
    return np.argmax(layer_2)

### Image relative path
# Project\Digit Recognition\data\testSet\testSet\img_8.jpg

### Module/Script relative path
# "Project\Digit Recognition\model.py"

### Sample script to run in cmd
### with root set to D:\OneDrive - nhg.vn\Self-study\Deep Learning
# python "Project\Digit Recognition\model.py" "Project\Digit Recognition\data\testSet\testSet\img_37.jpg"