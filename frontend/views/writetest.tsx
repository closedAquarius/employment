import { ViewConfig } from '@vaadin/hilla-file-router/types.js';
import {useEffect, useState} from "react";
import {AssistantService, ClientService} from "Frontend/generated/endpoints";
import InterViewRecords from "../generated/com/guangge/Interview/writtentest/InterViewRecord";
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
  const { name } = location.state;
  const [messages, setMessages] = useState<MessageItem[]>([{
    role: 'assistant',
    content: '欢迎您来到光哥面试系统! 请输入笔试者姓名。'
  }]);

  useEffect(() => {
    // Update bookings when we have received the full response
    console.log('name:' + name);
    if (name) {
        sendMessage(name);
    }
  }, [name]);

  function addMessage(message: MessageItem) {
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
    addMessage({
      role: 'user',
      content: message
    });
    let first = true;
    AssistantService.interViewChat(chatId, message)
      .onNext(token => {
        if (first && token) {
          addMessage({
            role: 'assistant',
            content: token
          });

          first = false;
        } else {
          appendToLatestMessage(token);
        }
      })
      .onError(() => setWorking(false))
      .onComplete(() => setWorking(false));
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