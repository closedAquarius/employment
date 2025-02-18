import { EndpointRequestInit as EndpointRequestInit_1 } from "@vaadin/hilla-frontend";
import type InterViewRecord_1 from "./com/guangge/Interview/test/WrittenTestTools/InterViewRecord.js";
import client_1 from "./connect-client.default.js";
async function getInterView_1(init?: EndpointRequestInit_1): Promise<Array<InterViewRecord_1>> { return client_1.call("ClientService", "getInterView", {}, init); }
async function sendMail_1(name: string, init?: EndpointRequestInit_1): Promise<void> { return client_1.call("ClientService", "sendMail", { name }, init); }
export { getInterView_1 as getInterView, sendMail_1 as sendMail };
