import { ViewConfig } from '@vaadin/hilla-file-router/types.js';
import {useEffect, useState} from "react";
import {AssistantService, ClientService} from "Frontend/generated/endpoints";
import CandidateRecord from "../generated/com/guangge/Interview/writtentest/CandidateRecord";
import {GridColumn} from "@vaadin/react-components/GridColumn";
import {Grid,type GridEventContext} from "@vaadin/react-components/Grid";
import {MessageInput} from "@vaadin/react-components/MessageInput";
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

export const config: ViewConfig = { menu: { order: 2, icon: 'vaadin:records' }, title: '候选者名单' };

const Candidates = () => {
  const [working, setWorking] = useState(false);
  const [candidates, setCandidates] = useState<CandidateRecord[]>([]);
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [searchTerm, setSearchTerm] = useState(''); // 输入框的值

  useEffect(() => {
    // Update bookings when we have received the full response
    if (!working) {
      fetch(`/frontend/candidates`)
            .then(response => {
              if (!response.ok) {
                throw new Error('获取候选人记录失败');
              }
              return response.json();
            })
            .then(data => setCandidates(data as CandidateRecord[]))
            .catch(error => {
              console.error('获取候选人记录时出错:', error);
            });
    }
  }, [working]);
  
const statusRenderer = (status: string) => (
      <span {...{ theme: `badge ${status === '等待' ? '' :status === '通知' ? 'error' : 'success'}` }}>
         {status}
      </span>
);

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

function employeeRenderer({ item: candidates }) {
  return (
    <HorizontalLayout style={{ alignItems: 'center' }} theme="spacing">
      <Avatar  img={candidates.pictureUrl} name={`${candidates.number} ${candidates.name}`} />

      <VerticalLayout style={{ lineHeight: 'var(--lumo-line-height-m)' }}>
        <span>
          {candidates.number} {candidates.name}
        </span>
        <span
          style={{ fontSize: 'var(--lumo-font-size-s)', color: 'var(--lumo-secondary-text-color)' }}
        >
          {candidates.email}
        </span>
      </VerticalLayout>
    </HorizontalLayout>
  );
}

return (
<div style={{ padding: '20px', fontFamily: 'Arial, sans-serif', height: '80%' }}>
      <div className="flex flex-col gap-m p-m box-border" style={{width: '100%', height: '100%'}} >
        <h3>候选者名单</h3>
        <Grid items={candidates} className="flex-shrink-0" theme="row-stripes">
          <GridColumn path="number" header="候选者" renderer={employeeRenderer} autoWidth/>
          <GridColumn header="状态" >
          {({ item: candidates }) => statusRenderer(candidates.status)}
          </GridColumn>

          <GridColumn  header="上传简历" >
              {({ item: candidates }) => (
                 <input type="file" onChange={(e) => handleUpload(candidates.number, e)} />
              )}
          </GridColumn>
          <GridColumn header="发送邀请码" >
                {({ item: candidates }) => (
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
        </Grid>
      </div>
</div>
  );
}

export default withAuth(Candidates);