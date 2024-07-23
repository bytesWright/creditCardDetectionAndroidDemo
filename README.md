# Credit Card Information Capture App

## Overview

This app is designed to demonstrate the process of capturing and processing credit card information using our V0.3 model. The model is trained using synthetic data generated with Blender, Python, and other open-source tools. It accurately detects traditional credit card designs, identifying the front and back faces, text lines, chip, and magnetic band.

The model, along with the dataset and tools used to create it, are freely available for download under the Apache License Version 2.0. You are welcome to modify and distribute these components as needed.

We kindly request that you notify us if you plan to use these components or modified versions in a product, research project, or any other initiative that adds value. This helps us maintain a record of users and allows us to acknowledge their contributions on our website. Here are some useful links:

- [Project site](http://localhost:3000/visionCardDocs/#/)
- [Data set generator](https://github.com/bytesWright/creditCardDetectionDSG)
- [Data set](https://huggingface.co/datasets/bytesWright/creditCardDetectionDS)
- [Models](https://huggingface.co/bytesWright/creditCardDetection)

## Features

### Image Display Area
At the top, there are two placeholders for the images of a credit card:
- **Front Image**: Placeholder for the front of the card.
- **Back Image**: Placeholder for the back of the card.

### Form Fields
Below the images, there are several form fields for entering credit card details:
- **Cardholder Name** (Field not captured).
- **Card Number**
- **Expiration Date (MM/YY)**
- **CVV**

### Buttons
- **Capture**: Opens a camera view to capture the image of the credit card.
- **Submit**: Currently does nothing.

### Capture Screen
The capture screen presents a clear rectangle where the card should be placed for capturing. It frames the credit card within the viewfinder. Once all the fields of interest are captured, or both sides of the card are presented, the camera view closes and returns to the form.

## Technical Details
The Android app extracts the region where a credit card is detected and performs OCR with a model provided by Google. Since the data is rather simple, most of the classification of the data can be done by conventional methods. The only field not captured is the name.

## License
This project is licensed under the Apache License Version 2.0.

## Contributions
We welcome contributions from the community. If you plan to use these components or modified versions in a product, research project, or any other initiative that adds value, please notify us. This helps us maintain a record of users and allows us to acknowledge your contributions on our website.

## Contact
For any inquiries or further information, please contact us at [bytesWright@isdavid.com](mailto:bytesWright@isdavid.com).

---

**Note**: This app is a demonstration of collecting and processing credit card details, possibly for payment or verification purposes.
