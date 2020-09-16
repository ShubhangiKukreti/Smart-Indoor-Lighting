# Smart-Indoor-Lighting
An android application to detect the intensity of light in a room using a smartphone and automate the switching on/off process. The collected data is also used to predict the room occupancy based on light intensity in the room using a Machine Learning model.

ANDROID APPLICATION
The application uses a smartphone's ambient light sensor to detect and measure illuminance in a room. Two smartphones were used to incraese the accuracy of the readings. The raeding are sent and stored in Google Firebase. A weather API is called used the **volley library** to get the sunset time of the day.

Based on the current time and the sunset time, following situations are actuated:
1. If it is current time is less than sunset time and the light inside the room is less than 300 lux, the brightness is increased.
2. If it is current time is less than sunset time and the light inside the room is more than 1000 lux, the brightness is decreased.
3. If it is past sunset, then the brightness is increased portraying the action of switching the lights on after sunset.

MACHINE LEARNING MODEL
A ML model is trained using **Logistic Regression** algorithm using open-source data to predict the occupancy of the room. The model is then tested on the light intensity values collected from the smartphones. 


TO-DO
Use the output of the ML model(room occupnacy prediction) to automate the switching on/off of lights action.
