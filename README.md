User Data EMI API
A Spring Boot REST API service that processes user credit data and provides EMI (Equated Monthly Installment) plans based on user profiles and bureau scores.
Overview
This application receives user purchase details, looks up their credit profile from a CSV database, combines the data, and returns personalized EMI plans with risk assessment. The service is designed to provide real-time credit decisions for e-commerce transactions.
Key Features: - REST API for EMI plan calculation - CSV-based user data lookup - Input validation and error handling - Structured JSON responses - Risk assessment and credit scoring
Prerequisites
	•	Java 17 or higher
	•	Maven 3.6+
	•	IDE (IntelliJ IDEA, Eclipse, or VS Code)
Installation & Setup
1. Clone/Download the Project
# Create project directory mkdir user-data-api cd user-data-api
2. Create Maven Project Structure
user-data-api/ ├── src/main/java/com/example/userdataapi/ ├── src/main/resources/ └── src/test/java/com/example/userdataapi/
3. Add User Data CSV File
Create a CSV with your user bureau data.
A sample file is provided = src/main/resources/financial_dummy_data.csv


4. Configure Application
Update src/main/resources/application.yml:
server:   port: 8080  spring:   application:     name: user-data-api  app:   csv:     file:       path: src/main/resources/financial_dummy_data.csv   external:     api:       url: https://your-production-api-endpoint.com/emi-calculator  logging:   level:     com.example.userdataapi: INFO

Running the Application
1. Build the Project
mvn clean install
2. Run the Application
mvn spring-boot:run
3. Verify Setup
The application will start on http://localhost:8080
Check health: GET http://localhost:8080/actuator/health (if actuator is enabled)
API Usage
Endpoint
POST /api/v1/process-user-data Content-Type: application/json

Request Body
{   "user_id": "U001",   "cart_value": 27999,   "merchant_category": "mobile",   "merchant_subvention_no_cost_emi_flag": false,   "live_address_info": {     "pincode": 400080,     "address_change_months": 2   } }

Response
{   "plans": [     {       "tenure": 3,       "apr": 0,       "monthly": 9333,       "reason": "Merchant subvention & stable profile"     },     {       "tenure": 6,       "apr": 6,       "monthly": 4830,       "reason": "Balanced plan"     }   ],   "ui_message": {     "headline": "Pay ₹9,333/mo",     "subcopy": "Pre-approved 3-month EMI for this purchase. Click to view plans."   },   "risk_explanation": "Low risk: stable address + high GMV; offered 0% for 3 months.",   "debug": {     "pincode_stability_score": 0.86,     "thin_file_flag": false,     "imputed_income": 52000   } }

CURL Example
curl -X POST http://localhost:8080/api/v1/process-user-data \
-H "Content-Type: application/json" \
-d '{     "user_id": "U001",     "cart_value": 27999,     "merchant_category": "mobile",     "merchant_subvention_no_cost_emi_flag": false,     "live_address_info": {       "pincode": 400080,       "address_change_months": 2     }   }'

Project Structure
src/main/java/com/example/userdataapi/ 
├── UserDataApiApplication.java          # Main application class 
├── controller/ │   └── UserDataController.java          # REST API endpoints 
├── dto/ 
│   ├── UserRequestDto.java              # Request data structure 
│   └── ExternalApiResponseDto.java      # Response data structure 
├── model/ 
│   ├── UserCsvData.java                 # CSV data mapping 
│   └── CombinedUserData.java           # Combined data model 
├── service/ │   ├── UserDataService.java            # Main business logic 
│   ├── CsvReaderService.java           # CSV data processing 
│   └── ExternalApiService.java         # External API integration 
└── exception/     
	├── GlobalExceptionHandler.java     # Error handling     
	├── UserNotFoundException.java      # Custom exceptions     
	├── CsvProcessingException.java     
	└── ExternalApiException.java

Configuration
Environment Variables
# CSV file path CSV_FILE_PATH=/path/to/your/user_data.csv  # External API endpoint EXTERNAL_API_URL=https://your-production-api.com/endpoint  # Server port SERVER_PORT=8080
Application Properties
Key configurations in application.yml: - app.csv.file.path: Path to user data CSV file - app.external.api.url: External EMI calculation service URL - server.port: Application port (default: 8080)

Error Handling
The API provides user-friendly error responses in the same format:
Validation Errors (400)
{   "plans": [],   "ui_message": {     "headline": "Validation Failed",     "subcopy": "Please check your input: User ID is required"   },   "risk_explanation": "Invalid input data provided",   "debug": { "pincode_stability_score": 0.0, "thin_file_flag": true, "imputed_income": 0 } }
User Not Found (404)
{   "plans": [],   "ui_message": {     "headline": "User Not Found",     "subcopy": "Sorry, we couldn't find your profile in our system."   },   "risk_explanation": "User ID not found in records",   "debug": { "pincode_stability_score": 0.0, "thin_file_flag": true, "imputed_income": 0 } }

