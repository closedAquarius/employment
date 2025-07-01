import { ViewConfig } from '@vaadin/hilla-file-router/types.js';
import {useEffect, useState} from "react";
import {AssistantService, ClientService} from "Frontend/generated/endpoints";
import InterViewRecords from "../generated/com/guangge/Interview/writtentest/InterViewRecord";
import {GridColumn} from "@vaadin/react-components/GridColumn";
import {Grid,type GridEventContext} from "@vaadin/react-components/Grid";
import {MessageInput} from "@vaadin/react-components/MessageInput";
import {nanoid} from "nanoid";
import {SplitLayout} from "@vaadin/react-components/SplitLayout";
import Message, {MessageItem} from "../components/Message";
import MessageList from "Frontend/components/MessageList";
import { Button, Notification, TextField } from '@vaadin/react-components';
import { Tooltip } from '@vaadin/react-components/Tooltip.js';
import { HorizontalLayout } from '@vaadin/react-components/HorizontalLayout.js';
import { VerticalLayout } from '@vaadin/react-components/VerticalLayout.js';
import { Avatar } from '@vaadin/react-components/Avatar.js';
import { TextArea } from '@vaadin/react-components/TextArea.js';
import newInterViewDialog from "./newInterViewDialog";
import withAuth from 'Frontend/components/withAuth';
import axios from 'axios';

export const config: ViewConfig = { menu: { order: 3, icon: 'vaadin:records' }, title: '面试结果' };

const InterviewResult = () => {
  const [chatId, setChatId] = useState(nanoid());
  const [working, setWorking] = useState(false);
  const [interViews, setInterView] = useState<InterViewRecord[]>([]);
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [searchTerm, setSearchTerm] = useState(''); // 输入框的值

  useEffect(() => {
    // Update bookings when we have received the full response
    if (!working) {
      fetch(`/frontend/interView`)
            .then(response => {
              if (!response.ok) {
                throw new Error('获取面试记录失败');
              }
              return response.json();
            })
            .then(data => setInterView(data as InterViewRecord[]))
            .catch(error => {
              console.error('获取面试记录时出错:', error);
            });
    }
  }, [working]);

const statusRenderer = (status: string) => (
      <span {...{ theme: `badge ${status === '等待' ? '' :status === '淘汰' ? 'error' : 'success'}` }}>
         {status}
      </span>
);

const tooltipGenerator = (context: GridEventContext<interViews>): string => {
  let text = '';

  const { column, item } = context;
  if (column && item && (column.path == 'evaluate') ) {
    text = item.evaluate;
  }

  if (column && item && (column.path == 'interviewEvaluate') ) {
      text = item.interviewEvaluate;
    }

  return text;
};

const handleUpload = async (number: String, event) => {
    const file = event.target.files[0];
    if (!file) {
      alert('请先选择简历');
      return;
    }

    const formData = new FormData();
    formData.append('file', file);
    formData.append('id', number);

    try {
      const response = await axios.post(`/fileupload/resume/upload`, formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      });
      alert('上传成功');
      console.log('Response:', response.data);
    } catch (error) {
      alert('上传失败');
      console.error('Error:', error);
    }
  };

function employeeRenderer({ item: interViews }) {
  return (
    <HorizontalLayout style={{ alignItems: 'center' }} theme="spacing">
      <Avatar  name={`${interViews.number} ${interViews.name}`} />

      <VerticalLayout style={{ lineHeight: 'var(--lumo-line-height-m)' }}>
        <span>
          {interViews.number} {interViews.name}
        </span>
        <span
          style={{ fontSize: 'var(--lumo-font-size-s)', color: 'var(--lumo-secondary-text-color)' }}
        >
          {interViews.email}
        </span>
      </VerticalLayout>
    </HorizontalLayout>
  );
}

const handleSearch = () => {
    fetch(`/frontend/findInterView?question=${searchTerm}`)
                .then(response => {
                  if (!response.ok) {
                    throw new Error('获取面试记录失败');
                  }
                  return response.json();
                })
                .then(data => setInterView(data as InterViewRecord[]))
                .catch(error => {
                  console.error('获取面试记录时出错:', error);
                });
  };

return (
<div style={{ padding: '20px', fontFamily: 'Arial, sans-serif', height: '80%' }}>
      <div style={{ display: 'flex', gap: '10px', marginBottom: '20px' }}>
        <TextField
          placeholder="输入搜索内容"
          style={{ flex: 1, padding: '8px', borderRadius: '4px', border: '1px solid #ccc' }}
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)} // 绑定输入框的值
        />
        <Button
          theme="primary"
          style={{ padding: '8px 16px', borderRadius: '4px', border: 'none', backgroundColor: '#007bff', color: '#fff' }}
          onClick={handleSearch}
        >
          查找
        </Button>
      </div>

      <div className="flex flex-col gap-m p-m box-border" style={{width: '100%', height: '100%'}} >
        <h3>候选者名单</h3>
        <Grid items={interViews} className="flex-shrink-0" theme="row-stripes">
          <GridColumn path="number" header="候选者" renderer={employeeRenderer} autoWidth/>
          <GridColumn header="结果" >
          {({ item: interView }) => statusRenderer(interView.interViewStatus)}
          </GridColumn>
          <GridColumn path="evaluate" header="笔试评语" >
            {({ item: interView  }) => (
                  <span>
                    {interView.evaluate}
                  </span>
                )}
          </GridColumn>
          <GridColumn path="interviewEvaluate" header="面试评语" >
            {({ item: interView  }) => (
                  <span>
                    {interView.interviewEvaluate}
                  </span>
                )}
          </GridColumn>
          <GridColumn header="发信" >
                {({ item: interView }) => (
                  <Button
                     onClick={() => {
                              fetch(`/frontend/sendMail`, {
                                        method: 'POST',
                                        headers: {
                                          'Content-Type': 'application/x-www-form-urlencoded',
                                        },
                                        body: new URLSearchParams({
                                          name: interView.name,
                                        }),
                                      })
                                        .then(response => {
                                          if (!response.ok) {
                                            throw new Error('发送邮件失败');
                                          }
                                          console.log('邮件发送成功');
                                        })
                                        .catch(error => {
                                          console.error('发送邮件时出错:', error);
                                        });
                            }}>
                     ✉
                  </Button>
                )}
          </GridColumn>
          <GridColumn  header="上传简历" >
              {({ item: interView }) => (
                 <input type="file" onChange={(e) => handleUpload(interView.number, e)} />
              )}
            </GridColumn>
          <Tooltip slot="tooltip" generator={tooltipGenerator} />
        </Grid>
      </div>
</div>
  );
}

export default withAuth(InterviewResult);