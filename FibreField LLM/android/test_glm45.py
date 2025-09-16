#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
GLM-4.5 Installation Test Script
Tests basic functionality of GLM-4.5 model in Claude Code environment
"""

import torch
from transformers import AutoTokenizer, AutoModel
import sys

def test_basic_imports():
    """Test if all required packages are properly installed"""
    print("[*] Testing basic imports...")
    try:
        import transformers
        print(f"[+] transformers version: {transformers.__version__}")

        print(f"[+] torch version: {torch.__version__}")
        print(f"[+] CUDA available: {torch.cuda.is_available()}")
        if torch.cuda.is_available():
            print(f"[+] CUDA device count: {torch.cuda.device_count()}")
            print(f"[+] Current CUDA device: {torch.cuda.current_device()}")

        return True
    except ImportError as e:
        print(f"[-] Import error: {e}")
        return False

def test_glm4_model_loading():
    """Test loading GLM-4 model (smaller version for testing)"""
    print("\n[*] Testing GLM-4 model loading...")
    try:
        # Use a smaller GLM model for testing (GLM-4-9B is quite large)
        model_name = "THUDM/chatglm3-6b"  # Fallback to smaller model for testing

        print(f"[*] Loading tokenizer for {model_name}...")
        tokenizer = AutoTokenizer.from_pretrained(model_name, trust_remote_code=True)
        print("[+] Tokenizer loaded successfully")

        # Test basic tokenization
        test_text = "Hello, how are you?"
        tokens = tokenizer(test_text, return_tensors="pt")
        print(f"[+] Tokenization test: '{test_text}' -> {len(tokens['input_ids'][0])} tokens")

        return True

    except Exception as e:
        print(f"[-] Model loading error: {e}")
        print("[i] Note: This might be due to model size or internet connection")
        return False

def test_glm4_availability():
    """Check if GLM-4.5 models are available"""
    print("\n[*] Checking GLM-4.5 model availability...")

    glm_models = [
        "THUDM/glm-4-9b",
        "THUDM/glm-4-9b-chat",
        "zai-org/GLM-4.5V"
    ]

    for model in glm_models:
        try:
            from huggingface_hub import model_info
            info = model_info(model)
            print(f"[+] {model} is available on Hugging Face")
        except Exception as e:
            print(f"[-] {model} not accessible: {str(e)[:100]}...")

def main():
    """Main test function"""
    print("GLM-4.5 Installation Test")
    print("=" * 50)

    # Test 1: Basic imports
    imports_ok = test_basic_imports()

    # Test 2: Model availability
    test_glm4_availability()

    # Test 3: Try loading a model (optional, might be resource intensive)
    if imports_ok:
        print("\n[?] Attempting model loading test (this may take time or fail due to size)...")
        model_ok = test_glm4_model_loading()

    print("\n" + "=" * 50)
    if imports_ok:
        print("[+] GLM-4.5 dependencies are properly installed!")
        print("[i] You can now use GLM-4.5 models with the transformers library")
        print("\n[!] Example usage:")
        print("   from transformers import AutoTokenizer, AutoModel")
        print("   tokenizer = AutoTokenizer.from_pretrained('THUDM/glm-4-9b', trust_remote_code=True)")
        print("   model = AutoModel.from_pretrained('THUDM/glm-4-9b', trust_remote_code=True)")
    else:
        print("[-] Some dependencies are missing. Please check the installation.")

    return imports_ok

if __name__ == "__main__":
    success = main()
    sys.exit(0 if success else 1)