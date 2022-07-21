import os
import pandas as pd

csv_folder = "/mnt/c/Users/simon/Documents/MTDS/sensorPOI"
columns = ["sensorID", "timestamp", "dist", "sensorID2", "timestamp2", "timestamp3", "long", "lat", "thresholdExceeded", "noiseVal", "poi", "region", "dist1"]
os.system("cat /mnt/c/Users/simon/Documents/MTDS/sensorPOI/*csv > /mnt/c/Users/simon/Documents/MTDS/combined.csv")

df = pd.read_csv("/mnt/c/Users/simon/Documents/MTDS/combined.csv")
df.columns = columns


print(df.sort_values(['timestamp'],ascending=False).groupby('sensorID').head(10))

