import { ViewConfig } from '@vaadin/hilla-file-router/types.js';
import {useEffect, useState} from "react";
import {GridColumn} from "@vaadin/react-components/GridColumn";
import {Grid} from "@vaadin/react-components/Grid";
import {MessageInput} from "@vaadin/react-components/MessageInput";
import {nanoid} from "nanoid";
import {SplitLayout} from "@vaadin/react-components/SplitLayout";
import Message, {MessageItem} from "../components/Message";
import MessageList from "Frontend/components/MessageList";
import { useNavigate, useLocation } from "react-router-dom";
import withAuth from 'Frontend/components/withAuth';

export const config: ViewConfig = {
  route: 'writetest',
  menu: {
    exclude: true,
  },
};

const Writetest = () => {
  const [chatId, setChatId] = useState(nanoid());
  const [working, setWorking] = useState(false);
  const location = useLocation();
  const name = localStorage.getItem('username');
  const [messages, setMessages] = useState<MessageItem[]>([{
    role: 'assistant',
    content: '欢迎您来到光哥面试系统! 请输入笔试者姓名。'
  }]);

  useEffect(() => {
    // Update bookings when we have received the full response

    if (name) {
        sendMessage(name);
    }
  }, [name]);

  function addMessage(message: MessageItem) {
    console.info('addMessage');
    setMessages(messages => [...messages, message]);
  }

  function appendToLatestMessage(chunk: string) {
    setMessages(messages => {
      const latestMessage = messages[messages.length - 1];
      latestMessage.content += chunk;
      return [...messages.slice(0, -1), latestMessage];
    });
  }

  async function sendMessage(message: string) {
    setWorking(true);

    // 添加用户消息
    addMessage({
      role: 'user',
      content: message,
    });

    // 初始化 EventSource
    const eventSource = new EventSource(
      `/interview/chat?chatId=${chatId}&userMessage=${encodeURIComponent(message)}`
    );

    let first = true;

    // 监听消息事件
    eventSource.onmessage = (event) => {
      const data = event.data; // 获取数据（已经是去掉 "data:" 前缀的）

      if (first) {
        // 如果是第一条消息，添加新消息
        addMessage({
          role: 'assistant',
          content: data,
        });
        first = false;
      } else {
        // 否则追加到最新消息
        appendToLatestMessage(data);
      }
    };

    // 监听错误事件
    eventSource.onerror = (event) => {
      console.error('EventSource failed:', event);
      setWorking(false); // 停止工作状态
      eventSource.close(); // 关闭连接
    };

    // 监听流结束事件（如果需要）
    eventSource.addEventListener('end', () => {
      setWorking(false); // 停止工作状态
      eventSource.close(); // 关闭连接
    });
  }

  return (
    <SplitLayout className="h-full">
      <div className="flex flex-col gap-m p-m box-border h-full" style={{width: '50%'}}>
        <h3>笔试题</h3>
        <MessageList messages={messages} className="flex-grow overflow-scroll msgp"  />
        <MessageInput onSubmit={e => sendMessage(e.detail.value)}  className="px-0" disabled={working} />
      </div>
    </SplitLayout>

  );
}

export default withAuth(Writetest);