# 🛡️ Guardian AI

### AI-Powered Family Safety & Digital Wellbeing Platform

> **Guardian AI** is a next-generation parental control platform designed to help parents protect, manage, and understand their children's digital lives through secure device management, intelligent monitoring, automation, and AI-powered insights.

<p align="center">
  <strong>Protect • Monitor • Understand • Guide</strong>
</p>

---

## ✨ Vision

Guardian AI aims to bring **parental controls, device management, digital wellbeing, security, and AI intelligence** into one simple ecosystem.

Instead of overwhelming parents with raw device data, Guardian AI transforms activity into **clear insights, useful alerts, and actionable recommendations**.

The platform is designed around:

* 🔐 Security & privacy
* 👨‍👩‍👧 Family safety
* 📱 Digital wellbeing
* 🤖 Responsible AI
* ⚡ Automation
* 🎯 Simple user experience

---

## 🚀 Core Features

### 📱 Device Management

* Connect multiple child devices
* Device pairing and verification
* Device online/offline status
* Battery monitoring
* Storage monitoring
* Internet connectivity status
* Remote device management

### ⏱️ Digital Wellbeing

* Screen-time monitoring
* Application usage statistics
* Daily/weekly usage reports
* App time limits
* Excessive usage detection
* Digital wellbeing insights

### 📍 Location Safety

* Real-time location tracking
* Location history
* Geofencing / Safe Zones
* Arrival alerts
* Departure alerts
* Location-based notifications

### 🛡️ Application & Content Protection

* View installed applications
* Allow/block applications
* Application usage reports
* Application time limits
* Website filtering
* Adult-content protection
* Safe Search enforcement
* Download restrictions

### 📂 File Management

* Browse child-device files
* Images
* Videos
* Audio
* Documents
* Other supported files
* Storage analysis
* File management where supported and permitted

### 🖥️ Screen Monitoring

* Live screen viewing
* Screenshot capture
* Screen recording
* Screen activity monitoring

> Availability depends on Android/iOS platform capabilities, permissions, and applicable policies.

### 📷 Camera & Audio

* Remote camera functionality
* Front/rear camera support
* Microphone functionality
* Optional recording

> These capabilities require explicit permissions and must comply with platform, privacy, and legal requirements.

---

# 🤖 AI Intelligence

Guardian AI is designed to turn device activity into meaningful information.

### AI-powered capabilities

* 🧠 Daily activity summaries
* 📊 AI-generated reports
* 🚨 Unusual activity detection
* ⏰ Excessive screen-time detection
* 📈 Usage-pattern analysis
* 🔮 Predictive digital-wellbeing alerts
* 💡 Personalized recommendations
* 📝 Natural-language family reports

### Example

Instead of showing a parent:

```text
YouTube: 2h 41m
Instagram: 1h 52m
Chrome: 1h 14m
Gaming: 2h 03m
```

Guardian AI can provide:

> **Today's Insight**
>
> Screen usage increased by 32% compared with the previous 7-day average. Gaming accounted for the largest increase, particularly between 9 PM and 11 PM.

The goal is to help parents **understand patterns rather than simply collect surveillance data**.

---

# 🏗️ System Architecture

```text
                    ┌──────────────────────┐
                    │      Parent App      │
                    │   Flutter / Android  │
                    └──────────┬───────────┘
                               │
                               │ HTTPS / API
                               ▼
                    ┌──────────────────────┐
                    │     FastAPI Backend  │
                    │   Authentication     │
                    │   Business Logic     │
                    │   Device APIs        │
                    └──────────┬───────────┘
                               │
              ┌────────────────┼────────────────┐
              │                │                │
              ▼                ▼                ▼
       ┌────────────┐   ┌────────────┐   ┌────────────┐
       │ PostgreSQL │   │   Storage  │   │  Firebase  │
       │ / Supabase │   │    Cloud   │   │    FCM     │
       └────────────┘   └────────────┘   └────────────┘
                               │
                               ▼
                    ┌──────────────────────┐
                    │      Child App       │
                    │   Android Service    │
                    └──────────────────────┘

                               │
                               ▼
                    ┌──────────────────────┐
                    │      AI Layer        │
                    │    OpenAI API        │
                    │  Insights & Reports  │
                    └──────────────────────┘
```

---

# 📦 Project Structure

```text
guardian-ai/
│
├── parent-app/
│   └──
│
├── child-app/
│   └──
│
├── backend/
│   ├── app/
│   ├── api/
│   ├── models/
│   ├── services/
│   ├── database/
│   ├── ai/
│   └── tests/
│
├── web-dashboard/
│   └──
│
├── docs/
│   ├── architecture/
│   ├── api/
│   ├── security/
│   └── product/
│
├── scripts/
│
├── .gitignore
├── README.md
└── LICENSE
```

---

# 🧩 Technology Stack

## Frontend

| Component         | Technology         |
| ----------------- | ------------------ |
| Parent Mobile App | Flutter / Android  |
| Child Mobile App  | Android / Kotlin   |
| Web Dashboard     | Next.js            |
| UI Prototyping    | Stitch / AI Studio |

## Backend

| Component      | Technology               |
| -------------- | ------------------------ |
| API            | FastAPI                  |
| Database       | PostgreSQL / Supabase    |
| Authentication | Firebase Authentication  |
| Notifications  | Firebase Cloud Messaging |
| Storage        | Cloud Storage            |
| AI             | OpenAI API               |
| Vector Search  | Qdrant / ChromaDB        |
| Automation     | n8n                      |

## Development

| Tool              | Purpose                |
| ----------------- | ---------------------- |
| GitHub            | Source control         |
| VS Code           | Development            |
| Android Studio    | Android development    |
| Docker            | Backend environments   |
| GitHub Actions    | CI/CD                  |
| Codex / AI Agents | Development assistance |

---

# 🔐 Security & Privacy

Guardian AI is designed with security and responsible monitoring as core principles.

### Security goals

* Secure authentication
* Device-pairing verification
* Encrypted communication
* Secure cloud storage
* Permission-based device capabilities
* Activity logging
* Role-based access
* Secure API authentication
* Protection against unauthorized device access

### Privacy principles

Guardian AI should:

1. Collect only necessary data.
2. Clearly communicate what is being monitored.
3. Require appropriate permissions.
4. Protect sensitive information.
5. Give users control over their data.
6. Follow applicable laws and platform policies.
7. Avoid hidden or unauthorized surveillance.

> Guardian AI is intended for legitimate parental/family safety use. Features involving camera, microphone, screen capture, files, location, or other sensitive device data must be implemented only where technically supported and legally/policy permitted.

---

# 👨‍👩‍👧 Family Model

```text
                    Parent Account
                         │
             ┌───────────┼───────────┐
             │           │           │
             ▼           ▼           ▼
          Child 1     Child 2     Child 3
             │           │           │
             ▼           ▼           ▼
          Device 1    Device 2    Device 3
```

A parent can manage multiple child devices according to the subscription plan.

---

# 🔗 Device Pairing

The platform will support secure device linking through mechanisms such as:

```text
Parent App
    │
    │ Generate pairing request
    ▼
Pairing Code / QR
    │
    ▼
Child App
    │
    │ Confirm permission
    ▼
Secure Device Registration
    │
    ▼
Backend
    │
    ▼
Parent ↔ Child Device Connection
```

---

# 📊 AI Data Pipeline

```text
Child Device
     │
     ▼
Activity Data
     │
     ▼
Backend Processing
     │
     ▼
Data Normalization
     │
     ▼
AI Analysis
     │
     ├── Usage Patterns
     ├── Screen Time
     ├── App Activity
     ├── Device Events
     └── Location Events
     │
     ▼
AI Insight Engine
     │
     ▼
Parent Dashboard
     │
     ├── Reports
     ├── Alerts
     └── Recommendations
```

---

# 🛣️ Development Roadmap

## Phase 1 — Foundation

* [x] Parent app UI prototype
* [ ] Child app UI prototype
* [ ] Project repository structure
* [ ] Backend foundation
* [ ] Database foundation
* [ ] Authentication
* [ ] Device pairing
* [ ] Basic device management
* [ ] Screen-time monitoring
* [ ] App usage monitoring
* [ ] Location tracking

## Phase 2 — Intelligence

* [ ] AI activity summaries
* [ ] AI daily reports
* [ ] Smart alerts
* [ ] Excessive usage detection
* [ ] Usage pattern analysis
* [ ] Personalized recommendations
* [ ] Automation engine

## Phase 3 — Advanced Monitoring

* [ ] Screen monitoring
* [ ] Screen recording
* [ ] Screenshot functionality
* [ ] Camera capabilities
* [ ] Audio capabilities
* [ ] Advanced file management
* [ ] Advanced device controls

## Phase 4 — Platform Expansion

* [ ] iOS support
* [ ] Advanced web dashboard
* [ ] Cross-platform family management
* [ ] Enterprise version
* [ ] School edition
* [ ] AI family assistant

---

# 🔄 Development Workflow

Guardian AI follows a **vertical feature development approach**.

```text
UI Prototype
     ↓
Project Foundation
     ↓
Mock-ready UI
     ↓
Authentication
     ↓
Device Pairing
     ↓
Child Capability Manager
     ↓
Complete Feature
     ↓
Parent UI
     ↓
Repository
     ↓
Backend API
     ↓
Database
     ↓
Child Service
     ↓
Android API
     ↓
Testing
     ↓
Git Commit
     ↓
Next Feature
```

Each major feature should ideally be completed end-to-end before moving to the next one.

---

# 🧪 Testing Strategy

Testing will eventually cover:

### Frontend

* UI testing
* Navigation testing
* State management
* Permission flows
* Error states

### Backend

* API tests
* Authentication tests
* Database tests
* Authorization tests
* Rate-limit tests

### Device

* Permission handling
* Background services
* Battery behavior
* Network changes
* Offline behavior
* Android-version compatibility

### Security

* Authentication bypass testing
* Authorization testing
* API security
* Device-pairing security
* Data-access validation

---

# 📈 Future AI Architecture

```text
             Device Events
                   │
                   ▼
            Event Processing
                   │
                   ▼
             Feature Store
                   │
        ┌──────────┴──────────┐
        ▼                     ▼
   Rule Engine            AI Engine
        │                     │
        └──────────┬──────────┘
                   ▼
              Risk Scoring
                   │
                   ▼
             Alert Engine
                   │
                   ▼
             Parent App
```

The system should combine **deterministic rules + statistical analysis + AI reasoning** rather than relying entirely on an LLM.

---

# 💰 Potential Business Model

Guardian AI can eventually support:

### Free

* Basic device management
* Basic screen-time reports
* Limited devices

### Premium

* Multiple devices
* Advanced reports
* Location history
* Smart alerts
* AI insights

### Family / Pro

* More devices
* Advanced automation
* Extended history
* Advanced AI features

### Future B2B

* Schools
* Educational institutions
* Family safety organizations
* Enterprise digital-wellbeing programs

---

# ⚠️ Important Engineering Principles

Guardian AI should prioritize:

```text
Security > Convenience
Privacy > Data Collection
Reliability > Feature Count
User Consent > Silent Monitoring
Explainability > Black-box Decisions
Simple UX > Feature Overload
```

AI recommendations should assist parents rather than make irreversible decisions automatically.

---

# 🤝 Contributing

The project is currently under active development.

Future contribution guidelines will cover:

* Branch naming
* Commit conventions
* Pull requests
* Code reviews
* Testing requirements
* Security reporting
* Documentation

---

# 📄 License

License information will be added when the project's distribution model is finalized.

---

# 🌟 Project Status

**Status:** 🚧 Active Development

**Current Focus:**
Parent App → Child App → Project Structure → Backend Foundation → Device Pairing → First Complete Feature

---

## 🛡️ Guardian AI

**Building safer digital experiences for families through technology, intelligence, and responsible monitoring.**

> **Protect. Understand. Guide.**
