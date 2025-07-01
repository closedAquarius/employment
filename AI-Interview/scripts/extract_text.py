import sys
import fitz  # PyMuPDF

def extract_text_from_pdf(pdf_path):
    # 打开PDF文件
    doc = fitz.open(pdf_path)
    text = ""
    # 逐页提取文本
    for page in doc:
        text += page.get_text()
    return text

if __name__ == "__main__":
    # 从命令行参数获取PDF文件路径
    pdf_path = sys.argv[1]
    # 提取文本并打印（使用utf-8编码）
    try:
        text = extract_text_from_pdf(pdf_path)
        print(text.encode("utf-8").decode("utf-8"))
    except Exception as e:
        print(f"Error: {e}", file=sys.stderr)
        sys.exit(1)