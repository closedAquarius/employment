import { _getPropertyModel as _getPropertyModel_1, makeObjectEmptyValueCreator as makeObjectEmptyValueCreator_1, NumberModel as NumberModel_1, ObjectModel as ObjectModel_1, StringModel as StringModel_1 } from "@vaadin/hilla-lit-form";
import type InterViewRecord_1 from "./InterViewRecord.js";
class InterViewRecordModel<T extends InterViewRecord_1 = InterViewRecord_1> extends ObjectModel_1<T> {
    static override createEmptyValue = makeObjectEmptyValueCreator_1(InterViewRecordModel);
    get number(): StringModel_1 {
        return this[_getPropertyModel_1]("number", (parent, key) => new StringModel_1(parent, key, true, { meta: { javaType: "java.lang.String" } }));
    }
    get name(): StringModel_1 {
        return this[_getPropertyModel_1]("name", (parent, key) => new StringModel_1(parent, key, true, { meta: { javaType: "java.lang.String" } }));
    }
    get score(): NumberModel_1 {
        return this[_getPropertyModel_1]("score", (parent, key) => new NumberModel_1(parent, key, false, { meta: { javaType: "int" } }));
    }
    get interViewStatus(): StringModel_1 {
        return this[_getPropertyModel_1]("interViewStatus", (parent, key) => new StringModel_1(parent, key, true, { meta: { javaType: "java.lang.String" } }));
    }
    get evaluate(): StringModel_1 {
        return this[_getPropertyModel_1]("evaluate", (parent, key) => new StringModel_1(parent, key, true, { meta: { javaType: "java.lang.String" } }));
    }
    get email(): StringModel_1 {
        return this[_getPropertyModel_1]("email", (parent, key) => new StringModel_1(parent, key, true, { meta: { javaType: "java.lang.String" } }));
    }
    get mp3path(): StringModel_1 {
        return this[_getPropertyModel_1]("mp3path", (parent, key) => new StringModel_1(parent, key, true, { meta: { javaType: "java.lang.String" } }));
    }
    get interviewEvaluate(): StringModel_1 {
        return this[_getPropertyModel_1]("interviewEvaluate", (parent, key) => new StringModel_1(parent, key, true, { meta: { javaType: "java.lang.String" } }));
    }
}
export default InterViewRecordModel;
