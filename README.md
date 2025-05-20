# Food Allergy Detector

![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Version](https://img.shields.io/badge/version-1.0-green.svg)
![Java](https://img.shields.io/badge/Java-17%2B-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2%2B-brightgreen.svg)
![Android](https://img.shields.io/badge/Android%20SDK-33%2B-brightgreen.svg)

## 🌱 About

Food Allergy Detector is an AI-powered mobile solution that enables instant food allergen risk assessment for consumers. By leveraging QR/barcode scanning, secure user allergen profiles, and real-time analysis through a Spring Boot backend, the system enables users to scan products and receive personalized allergy risk feedback. The system helps individuals with food allergies make safer dietary choices every day.

<p align="center">
  <img src="screenshots/homepage.jpg" alt="App Home Screen" width="220">
  <img src="screenshots/productscan.jpg" alt="Scan Result Screen" width="220">
  <img src="screenshots/profile.jpg" alt="Profile Screen" width="220">
</p>

## 🚀 Features

- **User Registration & Secure Login**: JWT-based authentication system
- **Allergen Profile Management**: Add, edit, or remove allergens from your personal list
- **Product Scanning**: Camera-based QR/barcode scanner for food products 
- **Real-Time Allergen Risk Evaluation**: Automatic matching of product ingredients with user allergens
- **Scan History**: Review past scan results for future reference
- **Clear Risk Feedback**: Color-coded results (Safe, Warning, Danger) with explanations

## 🏗️ Architecture

The system consists of two main components:

### Backend (Spring Boot)

- RESTful API built with Spring Boot
- JWT-based authentication and security
- Connection to OpenFoodFacts API for product data
- PostgreSQL database for user profiles and scan history
- Risk assessment algorithm for allergen matching

### Mobile Application (Android)

- Native Android application (Kotlin/Java)
- MVVM architecture for clean separation of UI and business logic
- Retrofit2 for API communication
- Room for local data caching
- Built-in barcode/QR scanner

<p align="center">
  <img src="screenshots/architecture-foodallergy.png" alt="System Architecture" width="700">
</p>

## 📋 Requirements

### Backend
- Java 17+
- Spring Boot 3.2+
- PostgreSQL
- Maven/Gradle

### Mobile Application
- Android Studio
- Android SDK 33+
- Gradle
- Retrofit
- Room
- ZXing (for barcode scanning)

## 🛠️ Installation

### Backend Setup

1. Clone the repository
   ```bash
   git clone https://github.com/sami3l/Food-Allergy-Detector.git
   cd Food-Allergy-Detector/backend
   ```

2. Configure environment variables
   - Create a `.env` file in the root directory
   - Add the following variables:
     ```
     DATABASE_URL=your_database_url
     JWT_SECRET=your_jwt_secret
     OPENFOODFACTS_API_KEY=your_api_key
     ```

3. Build and run
   ```bash
   ./mvnw spring-boot:run
   ```

4. The backend server should now be running at `http://localhost:8080`

### Mobile App Setup

1. Open the project in Android Studio
   ```bash
   cd Food-Allergy-Detector/mobile
   ```

2. Configure the API endpoint
   - Open `app/src/main/res/values/strings.xml`
   - Update the `api_base_url` value to your backend server address

3. Build and run on your device/emulator

## 🔍 Usage

1. **Registration**: Create an account with your email and password
2. **Allergen Setup**: Add your allergens to your profile
3. **Scanning**: Use the scan button on the home screen to scan food product barcodes
4. **Review Results**: Get instant feedback on allergen risks (Safe, Warning, Danger)
5. **History**: Access your scan history from the profile section

## 🔄 Data Flow

The system follows this workflow:

1. User registers, logs in, and sets their allergen list
2. On scanning a product, the app sends a secure request with the barcode to the backend
3. The backend fetches ingredient data from OpenFoodFacts, matches it with user allergens, and computes a risk level
4. The result is sent back and displayed instantly on the mobile app

## 📊 API Endpoints

The backend exposes the following RESTful API endpoints:

| Method | Endpoint                    | Description                             | Auth Required |
|--------|-----------------------------|-----------------------------------------|--------------|
| POST   | /api/auth/register          | Register a new user                     | No           |
| POST   | /api/auth/login             | Authenticate and get token              | No           |
| GET    | /api/allergens              | Get common allergens list               | No           |
| GET    | /api/user/allergens         | Get user's allergens                    | Yes          |
| POST   | /api/user/allergens         | Add allergen to user profile            | Yes          |
| DELETE | /api/user/allergens/{id}    | Remove allergen from user profile       | Yes          |
| POST   | /api/scan                   | Scan product and get allergen analysis  | Yes          |
| GET    | /api/scan/history           | Get user's scan history                 | Yes          |

For full API documentation, access Swagger UI at `/swagger-ui.html` when the backend is running.

<p align="center">
  <img src="screenshots/swagger.png" alt="API Documentation" width="700">
</p>

## 🧪 Testing

The system has been tested with:

- Automated backend tests (JUnit, integration tests)
- Manual user testing for usability and reliability
- End-to-end latency tests (scan to result typically <2 seconds)
- Testing with diverse food products and allergen profiles

## 🛡️ Security

- JWT authentication for API security
- HTTPS for secure data transmission
- Secure storage of allergen profiles
- No sensitive user data shared with external APIs

## 📝 Limitations

- Dependency on external databases (OpenFoodFacts)
- Requires products to have scannable codes
- Internet connection required for scanning
- Cannot yet analyze products with incomplete labeling

## 🔮 Future Work

- OCR functionality for label photo analysis
- iOS and web platform support
- Multi-language support for international users
- Offline mode for scanning without internet
- Personalized dietary recommendations
- AI-enhanced image recognition for products without codes

## 👥 Contributors

- **Sami Elhadraoui** - Software, Conceptualization, Documentation
- **Abdollah Habibi** - Software, Validation, Testing

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 📞 Contact

For questions or support, please email: sami.elhadraoui@emsi-edu.ma

---
© 2025 Food Allergy Detector
