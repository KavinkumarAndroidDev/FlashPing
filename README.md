# ⚡ FlashPing  
**Offline Light-Based Communication App**

FlashPing is a native Android app that enables **covert, short-range communication using light**.  
It transforms text messages into sequences of flashlight pulses (Morse code) and decodes incoming light signals into readable text — **no Wi-Fi, data, or Bluetooth required.**

---

## 🚀 Purpose
FlashPing is designed for environments where traditional communication channels are **unavailable, insecure, or need to stay silent**.  
It provides **offline, line-of-sight** data transfer using visual light — perfect for experimentation, learning, and privacy-focused tech enthusiasts.

---

## ✨ Key Features

### 🔐 Dual-Mode Encryption
- **Standard Morse Code:** Converts text into universal dots and dashes.  
- **Shadow Cipher:** A secure variant of Morse that increases privacy (custom logic not disclosed).

### ⚙️ Adjustable Speed Transmission
- Four modes — **Slow**, **Normal**, **Fast**, and **Rapid** — let users control how quickly the flashlight transmits pulses.

### 📷 Real-Time Optical Decryption
- Uses **CameraX** to detect and decode incoming light signals dynamically.  
- Features **adaptive time calibration** that automatically learns the sender’s transmission speed.  
- Includes a **live debug view** showing:
  - Current luminance (Luma)  
  - ON threshold detection  
  - Calibrated time unit in milliseconds  

### 🎨 Premium Cyber-Themed UI
- Built with a **“Premium-Tech” aesthetic** using a tri-tone palette:
  - **PremiumBlack**
  - **PremiumYellow**
  - **PremiumDarkGray**
- Custom-styled buttons, splash screens, and debug panels enhance the futuristic feel.

### 🧩 Modern Android Architecture
- **MVVM** pattern with shared ViewModels for clean state management.  
- **Jetpack Compose** for fully declarative UI.  
- **Kotlin Coroutines** for precise flashlight timing and background tasks.  
- **Kotlin Flow** for real-time state updates.  
- **Jetpack Navigation** for seamless transitions.  
- **Accompanist Permissions** for elegant camera permission handling.

---

## 🧠 Tech Stack

| Layer | Technologies Used |
|-------|--------------------|
| **Language** | Kotlin |
| **UI** | Jetpack Compose |
| **Architecture** | MVVM (ViewModel + StateFlow) |
| **Camera** | CameraX (ImageAnalysis) |
| **Async** | Kotlin Coroutines |
| **Navigation** | Jetpack Navigation |
| **Permissions** | Accompanist Permissions |
| **Build Tool** | Gradle |

---

## 📲 Screens
| Encryption Screen | Transmission Screen | Decryption Debug |
|--------------------|---------------------|------------------|
| Converts text to light pulses | Sends via flashlight | Reads incoming flashes in real time |

---

## 🧩 Project Structure

```

com.kkdev.flashping/
│
├── ui/                     # Jetpack Compose UI components
│   ├── screens/             # Encrypt, Transmit, Decrypt screens
│   └── theme/               # Colors, typography, and styles
│
├── viewmodel/               # EncryptViewModel, FlashlightViewModel
│
├── util/                    # Morse and cipher conversion utilities
│
├── camera/                  # CameraX analysis logic for light detection
│
└── MainActivity.kt           # Entry point and navigation setup

````

---

## 🧪 Highlights
- Camera-based real-time signal analysis  
- Adaptive timing logic for decoding variable-speed flashes  
- Immersive UI built entirely in Compose  
- Fully offline functionality  

---

## 🧰 Setup & Run

### Prerequisites
- Android Studio Ladybug or newer  
- Android SDK 33+  
- A device with **flashlight** and **camera** support

### Steps
```bash
git clone https://github.com/KavinkumarAndroidDev/FlashPing.git
cd FlashPing
````

* Open in **Android Studio**
* Connect a physical device (emulator won’t support flashlight)
* Build & Run

---

## 🧑‍💻 Author

**Kavinkumar R**
🔗 [GitHub](https://github.com/KavinkumarAndroidDev)

---

## 📦 Download

👉 [Latest Release](https://github.com/KavinkumarAndroidDev/FlashPing/releases)

---

## 📝 License

This project is licensed under the **MIT License** — free for learning, modification, and experimentation.

---

> ⚡ *FlashPing – bridging communication through light.*
