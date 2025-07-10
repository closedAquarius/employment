import pdfkit
import sys
import os
import subprocess

def html_to_pdf(html_content, pdf_file):
    try:
        # 尝试查找wkhtmltopdf的路径
        wkhtmltopdf_path = get_wkhtmltopdf_path()
        
        if wkhtmltopdf_path:
            config = pdfkit.configuration(wkhtmltopdf=wkhtmltopdf_path)
            print(f"Using wkhtmltopdf at: {wkhtmltopdf_path}")
        else:
            print("No explicit wkhtmltopdf path found, using system default")
            config = pdfkit.configuration()

        # 将 HTML 字符串转换为 PDF
        pdfkit.from_string(html_content, pdf_file, configuration=config)
        print(f"PDF successfully created at {pdf_file}")
    except Exception as e:
        print(f"An error occurred: {e}")
        # 如果生成失败，尝试使用备用方法
        try_fallback_method(html_content, pdf_file)

def get_wkhtmltopdf_path():
    """尝试多种方式查找wkhtmltopdf的路径"""
    # 预定义的可能路径
    possible_paths = [
        # Windows 路径
        'C:/Program Files/wkhtmltopdf/bin/wkhtmltopdf.exe',
        # macOS 常见路径
        '/usr/local/bin/wkhtmltopdf',
        '/opt/homebrew/bin/wkhtmltopdf',
        # 其他可能的路径
        '/usr/bin/wkhtmltopdf'
    ]
    
    # 首先检查预定义路径
    for path in possible_paths:
        if os.path.exists(path) and os.access(path, os.X_OK):
            return path
            
    # 尝试使用which命令查找（Unix/Linux/macOS）
    try:
        if os.name != 'nt':  # 非Windows系统
            result = subprocess.run(['which', 'wkhtmltopdf'], 
                                   stdout=subprocess.PIPE, 
                                   stderr=subprocess.PIPE, 
                                   text=True, 
                                   check=False)
            if result.returncode == 0 and result.stdout.strip():
                return result.stdout.strip()
    except Exception:
        pass
        
    # Windows下尝试使用where命令
    try:
        if os.name == 'nt':
            result = subprocess.run(['where', 'wkhtmltopdf'], 
                                   stdout=subprocess.PIPE, 
                                   stderr=subprocess.PIPE, 
                                   text=True, 
                                   check=False)
            if result.returncode == 0 and result.stdout.strip():
                return result.stdout.splitlines()[0].strip()
    except Exception:
        pass
            
    return None

def try_fallback_method(html_content, pdf_file):
    """如果pdfkit失败，尝试直接调用wkhtmltopdf命令"""
    try:
        # 创建临时HTML文件
        temp_html = f"{pdf_file}.temp.html"
        with open(temp_html, 'w', encoding='utf-8') as f:
            f.write(html_content)
        
        # 尝试直接调用wkhtmltopdf命令
        wkhtmltopdf_path = get_wkhtmltopdf_path() or 'wkhtmltopdf'
        cmd = [wkhtmltopdf_path, temp_html, pdf_file]
        
        print(f"尝试使用命令行: {' '.join(cmd)}")
        result = subprocess.run(cmd, 
                              stdout=subprocess.PIPE, 
                              stderr=subprocess.PIPE, 
                              text=True)
        
        # 清理临时文件
        if os.path.exists(temp_html):
            os.remove(temp_html)
        
        if result.returncode == 0:
            print(f"备用方法成功! PDF created at {pdf_file}")
        else:
            print(f"备用方法失败: {result.stderr}")
    except Exception as e:
        print(f"备用方法异常: {str(e)}")

if __name__ == "__main__":
    # 检查参数数量
    if len(sys.argv) < 3:
        print("Usage: python html2pdf.py <html_content> <pdf_file>")
        sys.exit(1)
        
    # 从命令行参数获取 HTML 内容和 PDF 文件路径
    html_content = sys.argv[1]  # HTML 字符串
    pdf_file = sys.argv[2]      # 输出的 PDF 文件路径
    
    # 确保输出目录存在
    os.makedirs(os.path.dirname(os.path.abspath(pdf_file)), exist_ok=True)
    
    html_to_pdf(html_content, pdf_file)