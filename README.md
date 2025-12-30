# Personal Knowledge Graph 🧠

A powerful GraphQL-based knowledge management system built with Spring Boot and PostgreSQL. Create interconnected notes, organize with tags, and build your personal knowledge graph.

## 🌟 Features

- **📝 Note Management**: Create, read, update, and delete notes with rich content
- **🏷️ Tagging System**: Organize notes with multiple tags for easy categorization
- **🔗 Bidirectional References**: Link notes together to build a knowledge graph
- **🔍 GraphQL API**: Flexible querying with exactly what you need
- **💾 PostgreSQL Database**: Reliable and scalable data storage
- **🐳 Docker Ready**: One-command deployment with Docker Compose
- **🎨 GraphiQL Interface**: Interactive API playground built-in

## 🏗️ Architecture

```
┌─────────────────┐
│   GraphiQL UI   │  ← Interactive API testing
└────────┬────────┘
         │
┌────────▼────────┐
│  Spring Boot    │  ← GraphQL API Layer
│   + GraphQL     │
└────────┬────────┘
         │
┌────────▼────────┐
│   PostgreSQL    │  ← Data Persistence
└─────────────────┘
```

## 🚀 Quick Start

### Prerequisites

- **Docker Desktop** (includes Docker Compose)
- **Java 17+** (only for local development)
- **Maven 3.9+** (only for local development)

### 1️⃣ Clone the Repository

```bash
git clone <your-repo-url>
cd knowledge-graph
```

### 2️⃣ Start the Application

```bash
# Build and start all services
docker-compose up --build

# Or run in background (detached mode)
docker-compose up -d --build
```

**What happens automatically:**
✅ PostgreSQL database starts
✅ Database `knowledge_graph` is created
✅ Spring Boot application builds
✅ JPA creates all tables: `notes`, `tags`, `note_tags`, `note_references`
✅ Application starts and connects to database

### 3️⃣ Access the Application

- **GraphiQL Interface**: http://localhost:8080/graphiql
- **Health Check**: http://localhost:8080/actuator/health

### 4️⃣ Test It!

Open GraphiQL (http://localhost:8080/graphiql) and run:

```graphql
query {
  notes {
    id
    title
  }
}
```

You should see an empty array `[]` - ready to create notes!

## 📊 Database Schema

### Auto-Created Tables

When you run `docker-compose up`, these tables are **automatically created**:

#### 1. `notes` Table
```sql
┌──────────────┬──────────────┬─────────────────┐
│ Column       │ Type         │ Description     │
├──────────────┼──────────────┼─────────────────┤
│ id           │ UUID         │ Primary Key     │
│ title        │ VARCHAR(255) │ Note title      │
│ content      │ TEXT         │ Note content    │
│ created_at   │ TIMESTAMP    │ Creation time   │
│ updated_at   │ TIMESTAMP    │ Last update     │
└──────────────┴──────────────┴─────────────────┘
```

#### 2. `tags` Table
```sql
┌──────────┬──────────────┬─────────────────┐
│ Column   │ Type         │ Description     │
├──────────┼──────────────┼─────────────────┤
│ id       │ UUID         │ Primary Key     │
│ name     │ VARCHAR(100) │ Tag name (unique)│
└──────────┴──────────────┴─────────────────┘
```

#### 3. `note_tags` Table (Many-to-Many Join)
```sql
┌──────────┬──────┬─────────────────┐
│ Column   │ Type │ Description     │
├──────────┼──────┼─────────────────┤
│ note_id  │ UUID │ FK → notes.id   │
│ tag_id   │ UUID │ FK → tags.id    │
└──────────┴──────┴─────────────────┘
```

#### 4. `note_references` Table (Graph Relationships)
```sql
┌──────────────┬──────┬─────────────────────┐
│ Column       │ Type │ Description         │
├──────────────┼──────┼─────────────────────┤
│ from_note_id │ UUID │ FK → notes.id       │
│ to_note_id   │ UUID │ FK → notes.id       │
└──────────────┴──────┴─────────────────────┘
```

**Note:** All tables are created automatically by Hibernate with `spring.jpa.hibernate.ddl-auto=update`

## 🎯 GraphQL API Reference

### Schema Overview

```graphql
type Note {
    id: ID!
    title: String!
    content: String!
    createdAt: String!
    updatedAt: String
    tags: [Tag!]!
    referencesTo: [Note!]!    # Notes this note references
    referencedBy: [Note!]!    # Notes that reference this note
}

type Tag {
    id: ID!
    name: String!
    notes: [Note!]!
}
```

### 📖 Queries

#### Get All Notes
```graphql
query {
  notes {
    id
    title
    content
    tags {
      name
    }
    referencesTo {
      id
      title
    }
  }
}
```

#### Get Single Note by ID
```graphql
query {
  note(id: "550e8400-e29b-41d4-a716-446655440000") {
    id
    title
    content
    createdAt
    tags {
      name
    }
  }
}
```

#### Get Notes by Tag
```graphql
query {
  notesByTag(tagName: "graphql") {
    id
    title
    tags {
      name
    }
  }
}
```

#### Get All Tags
```graphql
query {
  tags {
    id
    name
    notes {
      id
      title
    }
  }
}
```

#### Get Single Tag
```graphql
query {
  tag(name: "spring-boot") {
    name
    notes {
      title
    }
  }
}
```

### ✏️ Mutations

#### Create Note
```graphql
mutation {
  createNote(input: {
    title: "Understanding GraphQL"
    content: "GraphQL is a query language for APIs..."
    tagNames: ["graphql", "api", "tutorial"]
  }) {
    id
    title
    tags {
      name
    }
    createdAt
  }
}
```

**Response:**
```json
{
  "data": {
    "createNote": {
      "id": "abc123...",
      "title": "Understanding GraphQL",
      "tags": [
        { "name": "graphql" },
        { "name": "api" },
        { "name": "tutorial" }
      ],
      "createdAt": "2024-12-30T10:30:00"
    }
  }
}
```

#### Update Note
```graphql
mutation {
  updateNote(
    id: "abc123..."
    input: {
      title: "Understanding GraphQL - Updated"
      content: "New content here..."
    }
  ) {
    id
    title
    updatedAt
  }
}
```

#### Delete Note
```graphql
mutation {
  deleteNote(id: "abc123...")
}
```

**Response:**
```json
{
  "data": {
    "deleteNote": true
  }
}
```

#### Add Reference Between Notes
```graphql
mutation {
  addReference(
    fromNoteId: "note-1-id"
    toNoteId: "note-2-id"
  ) {
    id
    title
    referencesTo {
      id
      title
    }
  }
}
```

#### Remove Reference
```graphql
mutation {
  removeReference(
    fromNoteId: "note-1-id"
    toNoteId: "note-2-id"
  ) {
    id
    referencesTo {
      id
      title
    }
  }
}
```

#### Add Tag to Note
```graphql
mutation {
  addTag(
    noteId: "note-id"
    tagName: "important"
  ) {
    id
    tags {
      name
    }
  }
}
```

#### Remove Tag from Note
```graphql
mutation {
  removeTag(
    noteId: "note-id"
    tagName: "old-tag"
  ) {
    id
    tags {
      name
    }
  }
}
```

## 🧪 Testing Guide

### Manual Testing with GraphiQL

#### Test Scenario 1: Create a Knowledge Base

**Step 1:** Create your first note
```graphql
mutation {
  createNote(input: {
    title: "Spring Boot Basics"
    content: "Spring Boot makes it easy to create stand-alone applications."
    tagNames: ["spring-boot", "java"]
  }) {
    id
    title
  }
}
```
*Save the returned ID!*

**Step 2:** Create a related note
```graphql
mutation {
  createNote(input: {
    title: "Spring Data JPA"
    content: "JPA simplifies database access in Spring Boot."
    tagNames: ["spring-boot", "jpa", "database"]
  }) {
    id
    title
  }
}
```
*Save this ID too!*

**Step 3:** Link the notes
```graphql
mutation {
  addReference(
    fromNoteId: "first-note-id"
    toNoteId: "second-note-id"
  ) {
    title
    referencesTo {
      title
    }
  }
}
```

**Step 4:** Query the graph
```graphql
query {
  notes {
    title
    tags { name }
    referencesTo { title }
    referencedBy { title }
  }
}
```

#### Test Scenario 2: Tag-Based Organization

**Create notes with common tags:**
```graphql
mutation CreateNote1 {
  createNote(input: {
    title: "GraphQL Introduction"
    content: "GraphQL basics..."
    tagNames: ["graphql", "tutorial"]
  }) {
    id
  }
}

mutation CreateNote2 {
  createNote(input: {
    title: "Advanced GraphQL"
    content: "Advanced concepts..."
    tagNames: ["graphql", "advanced"]
  }) {
    id
  }
}
```

**Query by tag:**
```graphql
query {
  notesByTag(tagName: "graphql") {
    title
    tags { name }
  }
}
```

### Automated Testing

#### Run Unit Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=NoteServiceTest

# Run with coverage
mvn test jacoco:report
```

#### Integration Testing Example

Create `src/test/java/com/yourname/knowledgegraph/GraphQLIntegrationTest.java`:

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureGraphQlTester
class GraphQLIntegrationTest {
    
    @Autowired
    private GraphQlTester graphQlTester;
    
    @Test
    void shouldCreateNote() {
        String mutation = """
            mutation {
                createNote(input: {
                    title: "Test Note"
                    content: "Test Content"
                    tagNames: ["test"]
                }) {
                    id
                    title
                }
            }
            """;
        
        graphQlTester.document(mutation)
            .execute()
            .path("createNote.title")
            .entity(String.class)
            .isEqualTo("Test Note");
    }
}
```

### Database Verification

#### Connect to PostgreSQL
```bash
# Access database shell
docker-compose exec db psql -U postgres -d knowledge_graph

# List tables
\dt

# Query notes
SELECT * FROM notes;

# Query relationships
SELECT 
    n1.title as from_note,
    n2.title as to_note
FROM note_references nr
JOIN notes n1 ON nr.from_note_id = n1.id
JOIN notes n2 ON nr.to_note_id = n2.id;

# Exit
\q
```

### Performance Testing

#### Check N+1 Query Problem

Watch the logs while running:
```graphql
query {
  notes {
    title
    tags { name }
  }
}
```

Look for multiple SQL queries:
```
Hibernate: SELECT * FROM notes
Hibernate: SELECT * FROM tags WHERE note_id = ?
Hibernate: SELECT * FROM tags WHERE note_id = ?
Hibernate: SELECT * FROM tags WHERE note_id = ?
```

This is the **N+1 problem** - a learning opportunity for implementing DataLoader!

## 🛠️ Development Commands

```bash
# Start services
docker-compose up

# Start in background
docker-compose up -d

# View logs
docker-compose logs -f

# View application logs only
docker-compose logs -f app

# Stop services
docker-compose down

# Stop and remove volumes (deletes database data)
docker-compose down -v

# Rebuild after code changes
docker-compose up --build

# Restart just the application
docker-compose restart app

# Access database
docker-compose exec db psql -U postgres -d knowledge_graph
```

## 📁 Project Structure

```
knowledge-graph/
├── src/
│   ├── main/
│   │   ├── java/com/yourname/knowledgegraph/
│   │   │   ├── KnowledgeGraphApplication.java
│   │   │   ├── entity/
│   │   │   │   ├── Note.java
│   │   │   │   └── Tag.java
│   │   │   ├── repository/
│   │   │   │   ├── NoteRepository.java
│   │   │   │   └── TagRepository.java
│   │   │   ├── service/
│   │   │   │   ├── NoteService.java
│   │   │   │   └── TagService.java
│   │   │   ├── controller/
│   │   │   │   ├── NoteController.java
│   │   │   │   └── TagController.java
│   │   │   └── dto/
│   │   │       ├── CreateNoteInput.java
│   │   │       └── UpdateNoteInput.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── graphql/
│   │           └── schema.graphqls
│   └── test/
│       └── java/
├── docker-compose.yml
├── Dockerfile
├── pom.xml
└── README.md
```

## 🔧 Configuration

### Environment Variables

Edit `docker-compose.yml` to change:

```yaml
environment:
  POSTGRES_DB: knowledge_graph          # Database name
  POSTGRES_USER: postgres               # Username
  POSTGRES_PASSWORD: postgres           # Password
  SPRING_JPA_HIBERNATE_DDL_AUTO: update # create, update, validate, none
```

### Application Profiles

- **Default**: Production settings (docker)
- **Local**: For running without Docker

To use local profile:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

## 🐛 Troubleshooting

### Port 8080 Already in Use
```bash
# Find what's using the port
lsof -i :8080

# Change port in docker-compose.yml
ports:
  - "8081:8080"  # Use 8081 instead
```

### Database Connection Failed
```bash
# Check if database is running
docker-compose ps

# View database logs
docker-compose logs db

# Restart database
docker-compose restart db
```

### Application Won't Start
```bash
# View application logs
docker-compose logs app

# Common issues:
# 1. Database not ready - wait 30 seconds and retry
# 2. Port conflict - change port in docker-compose.yml
# 3. Build failed - check Java/Maven versions
```

### Reset Everything
```bash
# Stop and remove all data
docker-compose down -v

# Rebuild from scratch
docker-compose up --build
```

## 📈 Next Steps

1. **Implement DataLoader** - Fix N+1 query problem
2. **Add Search** - Full-text search on note content
3. **Add Authentication** - Secure your knowledge graph
4. **Add Pagination** - Handle large datasets
5. **Add Versioning** - Track note history
6. **Export/Import** - Backup your knowledge base

## 🤝 Contributing

This is a learning project. Feel free to:
- Report issues
- Suggest improvements
- Submit pull requests
- Fork and customize

## 📄 License

MIT License - Free to use for learning and personal projects

## 🙏 Acknowledgments

Built with:
- Spring Boot 3.2
- Spring for GraphQL
- PostgreSQL 16
- Docker

---

**Happy Knowledge Graphing! 🚀**

For questions or issues, check the troubleshooting section or create an issue on GitHub.

## 🧪 Postman Testing Guide

This section shows how to test the application's endpoints with Postman. It covers REST and GraphQL requests, environment setup, example request bodies, and Postman test scripts to capture and reuse IDs between requests.

### 1) Postman quick setup

1. Install Postman (https://www.postman.com/).
2. Create a new Environment (top-right ▶ New Environment) and add a variable:
   - `baseUrl` = `http://localhost:8080`

3. When making requests use `{{baseUrl}}` so you can switch environments easily (local, staging, etc.).
4. For all GraphQL requests set header `Content-Type: application/json`.

### 2) REST - Graph data endpoint

- Endpoint: `GET {{baseUrl}}/api/graph/data`
- Purpose: Returns a convenience payload for front-end graph rendering (nodes, links, stats).
- Example Request (GET) — no body required.
- Example Response shape (JSON):

```
{
  "nodes": [
    { "id": "<UUID>", "label": "Title or Tag", "type": "note|tag", "content": "...", "tags": ["t1","t2"], "referenceCount": 2 }
  ],
  "links": [
    { "source": "<UUID>", "target": "<UUID>", "type": "references|has-tag" }
  ],
  "stats": { "noteCount": 1, "tagCount": 1, "referenceCount": 0 }
}
```

Tip: Use this request to validate DB content quickly after creating notes/tags.

### 3) GraphQL — general notes

- GraphQL endpoint: `POST {{baseUrl}}/graphql`
- Header: `Content-Type: application/json`
- Body format (JSON):
  - `{"query": "<GRAPHQL_QUERY_OR_MUTATION>", "variables": { ... } }`
- Interactive UI: `{{baseUrl}}/graphiql` (handy to craft queries before copying to Postman)

### 4) Useful GraphQL requests for Postman (examples)

Below are ready-to-copy request bodies. In Postman: method POST, URL `{{baseUrl}}/graphql`, body -> raw -> JSON.

1) Query all notes

Body:
```
{
  "query": "{ notes { id title content createdAt tags { id name } } }",
  "variables": null
}
```

2) Query single note by id (use environment variable `noteId`)

Body:
```
{
  "query": "query ($id: ID!) { note(id: $id) { id title content createdAt tags { id name } referencedBy { id } referencesTo { id } } }",
  "variables": { "id": "{{noteId}}" }
}
```

3) Create a note (recommended: use variables and capture the returned id)

Body:
```
{
  "query": "mutation ($input: CreateNoteInput!) { createNote(input: $input) { id title tags { name } createdAt } }",
  "variables": { "input": { "title": "My note from Postman", "content": "Body text...", "tagNames": ["postman","test"] } }
}
```

Postman test script (Tests tab) to capture created note id into environment variable `noteId`:

```javascript
const json = pm.response.json();
if (json && json.data && json.data.createNote && json.data.createNote.id) {
  pm.environment.set("noteId", json.data.createNote.id);
  console.log('Saved noteId =', json.data.createNote.id);
}
```

4) Update a note

Body:
```
{
  "query": "mutation ($id: ID!, $input: UpdateNoteInput!) { updateNote(id: $id, input: $input) { id title updatedAt } }",
  "variables": { "id": "{{noteId}}", "input": { "title": "Updated title from Postman" } }
}
```

5) Delete a note

Body:
```
{
  "query": "mutation ($id: ID!) { deleteNote(id: $id) }",
  "variables": { "id": "{{noteId}}" }
}
```

6) Add a reference (make sure you have two notes and their IDs available in env vars `fromId` and `toId`)

Body:
```
{
  "query": "mutation ($from: ID!, $to: ID!) { addReference(fromNoteId: $from, toNoteId: $to) { id referencesTo { id title } } }",
  "variables": { "from": "{{fromId}}", "to": "{{toId}}" }
}
```

7) Remove a reference

Body:
```
{
  "query": "mutation ($from: ID!, $to: ID!) { removeReference(fromNoteId: $from, toNoteId: $to) { id referencesTo { id } } }",
  "variables": { "from": "{{fromId}}", "to": "{{toId}}" }
}
```

8) Add a tag to a note

Body:
```
{
  "query": "mutation ($noteId: ID!, $tagName: String!) { addTag(noteId: $noteId, tagName: $tagName) { id tags { id name } } }",
  "variables": { "noteId": "{{noteId}}", "tagName": "important" }
}
```

9) Remove a tag from a note

Body:
```
{
  "query": "mutation ($noteId: ID!, $tagName: String!) { removeTag(noteId: $noteId, tagName: $tagName) { id tags { id name } } }",
  "variables": { "noteId": "{{noteId}}", "tagName": "important" }
}
```

10) Query all tags

Body:
```
{
  "query": "{ tags { id name notes { id title } } }",
  "variables": null
}
```

11) Query notes by tag

Body:
```
{
  "query": "query ($tagName: String!) { notesByTag(tagName: $tagName) { id title tags { name } } }",
  "variables": { "tagName": "postman" }
}
```

### 5) Chaining requests & example workflow in Postman

A typical end-to-end flow:
1. Run Create Note request — in Tests extract `noteId` into environment (see script above).
2. Run Create another Note, extract `otherNoteId` into environment variable.
3. Run Add Reference (use `from = {{noteId}}`, `to = {{otherNoteId}}`).
4. Run GET `{{baseUrl}}/api/graph/data` to inspect the graph structure.
5. Run Update or Delete requests as needed.

Folder/collection suggestion in Postman:
- Collection: Personal Knowledge Graph
  - Folder: GraphQL → Queries
  - Folder: GraphQL → Mutations
  - Folder: REST → Graph Data

### 6) Helpful Postman test scripts

- Save the first element id from a query result (example for `notes` query):

```javascript
const json = pm.response.json();
if (json && json.data && json.data.notes && json.data.notes.length) {
  pm.environment.set('noteId', json.data.notes[0].id);
}
```

- Assert that a GraphQL response contains `errors` and fail the test if present:

```javascript
const json = pm.response.json();
pm.test('No GraphQL errors', function() {
  pm.expect(json.errors).to.be.undefined;
});
```

### 7) Troubleshooting & tips

- If you get CORS or network errors when testing from a different host, ensure the app is reachable and the port (8080) is exposed.
- Use `{{baseUrl}}/graphiql` to prototype queries and copy them into Postman.
- IDs are UUIDs — copy/paste carefully; use environment variables to avoid mistakes.
- If using Docker, ensure `docker-compose up` completed successfully and the Spring Boot app is healthy.

---

If you'd like, I can also:
- Export a ready-to-import Postman collection JSON for you (I can generate one and add it to the repo).
- Add a small `curl` script folder with quick commands to test the same endpoints from the terminal.
