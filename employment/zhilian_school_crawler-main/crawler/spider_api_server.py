import logging

from fastapi import FastAPI
from pydantic import BaseModel
from crawler import run_spider
from crawler.match import Match


class CrawlRequest(BaseModel):
    auto: bool = False

app = FastAPI(
    title="Crawler API",
    description="启动爬虫的 REST 接口",
    version="1.0.0"
)

logger = logging.getLogger("uvicorn.error")
@app.post("/crawl")
async def crawl(auto: bool = False):
    logger.info(f"🌐 Received /crawl request, auto={auto}")
    # 构造一个“伪” args 对象，兼容原有 run_spider 签名
    class Args: ...
    args = Args()
    args.auto = auto

    # 调用原 crawler.py 里的函数
    run_spider(args)
    return {"status": "started", "auto": auto}


if __name__ == "__main__":
    import uvicorn
    uvicorn.run("api_server:app", host="0.0.0.0", port=8000, reload=True)
