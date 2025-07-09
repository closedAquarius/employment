import pdfkit
import sys
import os

def html_to_pdf(html_content, pdf_file):
    try:
        # 根据操作系统选择合适的配置
        if os.name == 'nt':  # Windows
            config = pdfkit.configuration(wkhtmltopdf='C:/Program Files/wkhtmltopdf/bin/wkhtmltopdf.exe')
        else:  # macOS 或 Linux
            # macOS上通常安装在/usr/local/bin/wkhtmltopdf
            if os.path.exists('/usr/local/bin/wkhtmltopdf'):
                config = pdfkit.configuration(wkhtmltopdf='/usr/local/bin/wkhtmltopdf')
            elif os.path.exists('/opt/homebrew/bin/wkhtmltopdf'):
                config = pdfkit.configuration(wkhtmltopdf='/opt/homebrew/bin/wkhtmltopdf')
            else:
                # 如果找不到wkhtmltopdf，尝试使用系统PATH中的wkhtmltopdf
                config = pdfkit.configuration()

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