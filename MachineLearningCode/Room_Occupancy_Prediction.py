import pandas as pd
import plotly_express as px
import pandas as pd
from scipy.special import expit
from matplotlib import pyplot as plt
from pandas import read_csv
from matplotlib import pyplot
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score
from sklearn.linear_model import LogisticRegression

data1 = read_csv('datatest.txt', header=0, index_col=1, parse_dates=True, squeeze=True)
data2 = read_csv('datatraining.txt', header=0, index_col=1, parse_dates=True, squeeze=True)
data3 = read_csv('datatest2.txt', header=0, index_col=1, parse_dates=True, squeeze=True)
# determine the number of features
# vertically stack and maintain temporal order
data_open = pd.concat([data1, data2, data3])
# drop row number
data_open.drop('no', axis=1, inplace=True)
# save aggregated dataset
data_open.to_csv('combined.csv')

data_open = read_csv('combined.csv', header=0, index_col=0, parse_dates=True, squeeze=True)
values = data_open.values
X, y = values[:, 2].reshape((len(values), 1)), values[:, -1]
# split the dataset
trainX, testX, trainy, testy = train_test_split(X, y, test_size=0.3, shuffle=False, random_state=1)
# define the model
model = LogisticRegression()
# fit the model on the training set
model.fit(trainX, trainy)
#print(model.coef_)
#print(model.intercept_)
# predict the test set
yhat = model.predict(testX)
score = accuracy_score(testy, yhat)

data = pd.read_csv("urban4-combined-data-result.csv")
sigmoid_function = expit(data['Sensor Value Mean'] * model.coef_[0][0] + model.intercept_[0]).ravel()
plt.plot(data['Sensor Value Mean'], sigmoid_function, '.')
plt.xlabel('Light Intensity')
plt.ylabel('Room Occupancy')
plt.scatter(data["Sensor Value Mean"], data["Room Occupancy"], c=data['Room Occupancy'], cmap='rainbow')
plt.show()
