# StudyMate – AI-Powered Study Workspace

StudyMate is an AI-powered study workspace designed to help students learn from their study materials. Students can upload PDF documents, ask questions about their content, receive AI-generated answers with relevant document sources, manage conversations, and save useful information as notes.

The project uses Retrieval-Augmented Generation (RAG) to generate answers grounded in uploaded documents.

## Features

### Implemented

- **User Authentication:** Student registration and login using Spring Security, BCrypt password hashing, and JWT authentication.
- **Workspaces:** Create and manage individual study workspaces.
- **PDF Upload:** Upload and validate PDF documents.
- **Text Extraction:** Extract text and page information from PDFs using Apache PDFBox.
- **Document Chunking:** Split extracted text into smaller overlapping chunks.
- **Embeddings:** Generate document embeddings using Google's Gemini embedding API.
- **Semantic Search:** Retrieve relevant document chunks using vector similarity.
- **RAG Chat:** Ask questions about uploaded documents and receive AI-generated answers grounded in retrieved content.
- **Source References:** Include retrieved chunk information and page references with AI responses.
- **Conversation Management:** Create conversations, send messages, retrieve chat history, and delete conversations.
- **Notes:** Create, view, update, and delete notes. Save AI-generated responses as notes.

### Planned Improvements

- React-based frontend.
- Hybrid retrieval using BM25 and vector search.
- Reranking of retrieved document chunks.
- Improved document chunking and retrieval quality.
- AI-generated quizzes and flashcards.
- User activity tracking.
- Production deployment and persistent cloud storage.

## Tech Stack

| Layer | Technologies |
|---|---|
| Backend | Java, Spring Boot |
| Security | Spring Security, JWT, BCrypt |
| Database | MySQL |
| Database Migrations | Flyway |
| ORM | Spring Data JPA, Hibernate |
| PDF Processing | Apache PDFBox |
| AI Integration | Google Gemini API |
| Embeddings | Gemini Embedding API |
| Build Tool | Maven |
| API Testing | Postman |
| Version Control | Git, GitHub |

## Architecture Overview

StudyMate follows a layered backend architecture.

```text
Client (Postman / Future React Frontend)
                  |
                  v
             Controllers
                  |
                  v
               Services
                  |
          +-------+-------+
          |               |
          v               v
     Repositories     Gemini API
          |
          v
        MySQL

PDF Upload
    |
    v
PDF Text Extraction
    |
    v
Text Chunking
    |
    v
Embedding Generation
    |
    v
MySQL (Chunks and Embeddings)

User Question
    |
    v
Question Embedding
    |
    v
Semantic Retrieval
    |
    v
Relevant Document Chunks
    |
    v
Gemini Chat API
    |
    v
Answer with Source References
```

## RAG Workflow

1. A student uploads a PDF to a workspace.
2. Apache PDFBox extracts text and page information.
3. The extracted text is divided into smaller overlapping chunks.
4. Gemini generates embeddings for the chunks.
5. The chunks and their embeddings are stored in MySQL.
6. When a student asks a question, StudyMate generates an embedding for the question.
7. The system retrieves relevant document chunks using cosine similarity.
8. The retrieved context and question are sent to Gemini.
9. Gemini generates an answer based on the retrieved document content.
10. The answer and source references are stored in the conversation history.

## Project Structure

```text
src/
└── main/
    ├── java/com/studymate/studymate/
    │   ├── auth/
    │   ├── user/
    │   ├── workspace/
    │   ├── document/
    │   ├── conversation/
    │   ├── note/
    │   └── common/
    │
    └── resources/
        ├── application.properties
        └── db/
            └── migration/
```

The backend follows a layered structure with controllers, services, repositories, entities, and DTOs.

## Prerequisites

Make sure you have the following installed:

- Java 25
- MySQL 8.0 or later
- Git
- IntelliJ IDEA or another Java IDE
- A Google Gemini API key

## Getting Started

### 1. Clone the repository

```bash
git clone YOUR_GITHUB_REPOSITORY_URL
cd studymate
```

Replace `YOUR_GITHUB_REPOSITORY_URL` with your actual repository URL.

### 2. Create the MySQL database

Open MySQL Workbench or your MySQL terminal and execute:

```sql
CREATE DATABASE studymate;
```

Flyway manages the database schema through migration scripts when the application starts.

### 3. Configure environment variables

Configure the following environment variables before starting the backend:

| Variable | Description |
|---|---|
| `DB_USERNAME` | MySQL username |
| `DB_PASSWORD` | MySQL password |
| `GEMINI_API_KEY` | Google Gemini API key |

The application reads these values from the environment rather than storing credentials directly in the source code.

### 4. Configure application properties

Ensure your `application.properties` contains the appropriate environment variable references.

For example:

```properties
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

gemini.api.key=${GEMINI_API_KEY}
```

Keep your actual credentials private. Never commit API keys or database passwords to GitHub.

### 5. Run the application

On Windows:

```bash
mvnw.cmd spring-boot:run
```

Alternatively, use IntelliJ IDEA to run the main Spring Boot application class.

The backend starts at:

```text
http://localhost:8080
```

## API Overview

The backend exposes REST APIs for authentication, workspaces, document management, conversations, and notes.

| Feature | Endpoint | Method |
|---|---|---|
| Document upload | `/api/workspaces/{workspaceId}/documents` | POST |
| Create note | `/api/workspaces/{workspaceId}/notes` | POST |
| Get workspace notes | `/api/workspaces/{workspaceId}/notes` | GET |
| Save AI response as note | `/api/messages/{messageId}/save-note` | POST |
| Get note | `/api/notes/{noteId}` | GET |
| Update note | `/api/notes/{noteId}` | PUT |
| Delete note | `/api/notes/{noteId}` | DELETE |

Most protected endpoints require a JWT access token in the Authorization header:

```http
Authorization: Bearer YOUR_JWT_TOKEN
```

## Security

- Passwords are hashed using BCrypt.
- JWT-based authentication protects secured endpoints.
- Spring Security handles authentication and authorization.
- Workspace and resource ownership are verified before access.
- Gemini API credentials remain on the backend.
- Uploaded PDF files are validated before processing.

## Testing

Backend APIs have been tested using Postman.

Testing includes:

- User registration and login.
- Workspace creation and access.
- PDF upload and processing.
- Document retrieval and RAG-based question answering.
- Conversation creation and message history.
- Note creation, updating, retrieval, and deletion.
- Saving AI-generated responses as notes.
- Authentication and cross-user access restrictions.
- Invalid and unsupported PDF uploads.

## Future Scope

StudyMate will be extended with a React and TypeScript frontend, improved RAG retrieval techniques, AI-generated quizzes and flashcards, and production deployment with persistent cloud storage.

## Author

Developed as a personal full-stack project to explore backend engineering, AI integration, and Retrieval-Augmented Generation.

---

**StudyMate – Making document-based learning smarter.**