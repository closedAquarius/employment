import os
import sys
import io
import spacy
import re

# 确保 Python 输出是 UTF-8，防止 Java 读取乱码
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

# 设置环境变量，防止 sys.argv 乱码（仅在 Windows 适用）
os.environ["PYTHONIOENCODING"] = "utf-8"

def clean_text(text):
    """清理文本，移除HTML标签和特殊字符"""
    text = re.sub(r"<.*?>", "", text)  # 移除HTML标签
    text = re.sub(r"[^\w\s\u4e00-\u9fff]", "", text)  # 仅保留中文、数字、字母
    return text.strip()

def extract_keywords(jd_text):
    """提取 JD 文本中的关键词"""
    # 清理文本
    jd_text = clean_text(jd_text)

    # 加载 spaCy 中文模型
    try:
        nlp = spacy.load("zh_core_web_sm")
    except OSError:
        print("错误: 未找到 spaCy 中文模型，请先运行 'python -m spacy download zh_core_web_sm'")
        sys.exit(1)

    # 定义停用词
    custom_stop_words = {"的", "是", "在", "了", "和", "我们", "你们", "他们", "这个"}
    for word in custom_stop_words:
        nlp.vocab[word].is_stop = True

    # 解析文本
    doc = nlp(jd_text)

    # 提取关键词（名词、形容词、动词）
    keywords = [token.text for token in doc if token.text.strip() and not token.is_stop and token.pos_ in {"NOUN", "ADJ", "VERB"}]

    return keywords

if __name__ == "__main__":
    # 获取输入参数
    if len(sys.argv) < 2:
        print("请提供JD文本作为参数")
        sys.exit(1)

    jd_text = sys.argv[1]

    # 确保命令行参数是UTF-8
    if isinstance(jd_text, bytes):
        jd_text = jd_text.decode("utf-8", errors="ignore")

    # 提取关键词
    keywords = extract_keywords(jd_text)

    # 输出关键词（逗号分隔）
    print(",".join(keywords))
