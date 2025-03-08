import { Subscription as Subscription_1 } from "@vaadin/hilla-frontend";
import client_1 from "./connect-client.default.js";
function interViewChat_1(chatId: string, userMessage: string): Subscription_1<string> { return client_1.subscribe("AssistantService", "interViewChat", { chatId, userMessage }); }
export { interViewChat_1 as interViewChat };
