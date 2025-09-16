#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
GLM-4.5 Usage Example Script
Demonstrates how to use GLM-4.5 models with the transformers library in Claude Code
"""

import torch
from transformers import AutoTokenizer, AutoModel

def load_glm_model(model_name="THUDM/glm-4-9b"):
    """Load GLM model and tokenizer"""
    print(f"[*] Loading GLM model: {model_name}")

    try:
        # Load tokenizer
        tokenizer = AutoTokenizer.from_pretrained(
            model_name,
            trust_remote_code=True
        )
        print("[+] Tokenizer loaded successfully")

        # Load model (this may take time and requires significant memory)
        model = AutoModel.from_pretrained(
            model_name,
            trust_remote_code=True,
            torch_dtype=torch.float16,  # Use half precision to save memory
            device_map="auto"           # Automatically distribute across available devices
        )
        print("[+] Model loaded successfully")

        return tokenizer, model

    except Exception as e:
        print(f"[-] Error loading model: {e}")
        return None, None

def chat_with_glm(tokenizer, model, query, history=None):
    """Simple chat function with GLM model"""
    if history is None:
        history = []

    try:
        # Prepare input
        inputs = tokenizer.apply_chat_template(
            [{"role": "user", "content": query}] + history,
            tokenize=False,
            add_generation_prompt=True
        )

        # Tokenize
        model_inputs = tokenizer([inputs], return_tensors="pt").to(model.device)

        # Generate response
        with torch.no_grad():
            generated_ids = model.generate(
                model_inputs.input_ids,
                max_new_tokens=512,
                do_sample=True,
                temperature=0.7,
                top_p=0.8,
                pad_token_id=tokenizer.eos_token_id
            )

        # Decode response
        generated_ids = [
            output_ids[len(input_ids):] for input_ids, output_ids in zip(model_inputs.input_ids, generated_ids)
        ]

        response = tokenizer.batch_decode(generated_ids, skip_special_tokens=True)[0]

        return response

    except Exception as e:
        print(f"[-] Error during generation: {e}")
        return None

def main():
    """Main example function"""
    print("GLM-4.5 Usage Example")
    print("=" * 40)

    # Model options (choose based on your hardware capabilities)
    models = {
        "1": "THUDM/chatglm3-6b",      # Smaller, faster, good for testing
        "2": "THUDM/glm-4-9b",         # Larger, better performance
        "3": "THUDM/glm-4-9b-chat"     # Chat-optimized version
    }

    print("Available models:")
    for key, model in models.items():
        print(f"  {key}. {model}")

    choice = input("\nSelect model (1-3, default=1): ").strip() or "1"
    model_name = models.get(choice, models["1"])

    print(f"\n[*] Selected model: {model_name}")

    # Load model
    tokenizer, model = load_glm_model(model_name)

    if tokenizer is None or model is None:
        print("[-] Failed to load model. Exiting.")
        return

    print("\n[+] Model ready for chat!")
    print("Type 'quit' to exit\n")

    # Interactive chat loop
    history = []
    while True:
        try:
            query = input("You: ").strip()

            if query.lower() in ['quit', 'exit', 'q']:
                break

            if not query:
                continue

            print("GLM: ", end="", flush=True)
            response = chat_with_glm(tokenizer, model, query, history)

            if response:
                print(response)
                # Add to history for context
                history.append({"role": "user", "content": query})
                history.append({"role": "assistant", "content": response})
                # Keep history limited
                if len(history) > 10:
                    history = history[-10:]
            else:
                print("Sorry, I couldn't generate a response.")

        except KeyboardInterrupt:
            print("\n\n[*] Chat interrupted. Goodbye!")
            break
        except Exception as e:
            print(f"\n[-] Error: {e}")
            continue

if __name__ == "__main__":
    # Check CUDA availability
    if torch.cuda.is_available():
        print(f"[+] CUDA available with {torch.cuda.device_count()} device(s)")
        print(f"[+] Current device: {torch.cuda.get_device_name()}")
    else:
        print("[!] CUDA not available, using CPU (will be slower)")

    main()