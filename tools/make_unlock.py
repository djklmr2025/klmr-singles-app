#!/usr/bin/env python3
"""Cifra el enlace de descargas con el codigo secreto (AES-256-GCM + PBKDF2).
Uso: python3 tools/make_unlock.py "CODIGO" "https://enlace-de-descarga.com/..."
Escribe app/src/main/assets/unlock.json (el enlace nunca queda en texto plano en la app)."""
import sys, os, json, base64, hashlib
from cryptography.hazmat.primitives.ciphers.aead import AESGCM
code, url = sys.argv[1].strip(), sys.argv[2].strip()
salt, iv = os.urandom(16), os.urandom(12)
key = hashlib.pbkdf2_hmac("sha1", code.encode("utf-8"), salt, 120000, 32)
ct = AESGCM(key).encrypt(iv, url.encode("utf-8"), None)
b = lambda x: base64.b64encode(x).decode()
os.makedirs("app/src/main/assets", exist_ok=True)
json.dump({"s": b(salt), "i": b(iv), "c": b(ct)}, open("app/src/main/assets/unlock.json", "w"))
print("unlock.json generado")

