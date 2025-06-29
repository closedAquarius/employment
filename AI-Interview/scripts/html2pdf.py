import pdfkit
import sys

def html_to_pdf(html_content, pdf_file):
    try:
        # 配置 wkhtmltopdf 的路径
        config = pdfkit.configuration(wkhtmltopdf='C:/Program Files/wkhtmltopdf/bin/wkhtmltopdf.exe')  # Windows 路径

        # 将 HTML 字符串转换为 PDF
        pdfkit.from_string(html_content, pdf_file, configuration=config)
        print(f"PDF successfully created at {pdf_file}")
    except Exception as e:
        print(f"An error occurred: {e}")

if __name__ == "__main__":
    # 从命令行参数获取 HTML 内容和 PDF 文件路径
    html_content = sys.argv[1]  # HTML 字符串
    pdf_file = sys.argv[2]      # 输出的 PDF 文件路径
    html_to_pdf(html_content, pdf_file)