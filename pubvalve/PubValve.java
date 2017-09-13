/*
 *
 *
 * Distributed under the OpenDDS License.
 * See: http://www.opendds.org/license.html
 */

import DDS.*;
import OpenDDS.DCPS.*;
import org.omg.CORBA.StringSeqHolder;
import Nexmatix.*;

public class PubValve {

    private static final int N_MSGS = 40;

    private static final int VALVE_PARTICIPANT = 23;
    private static final String VALVE_TOPIC = "Valve";

    public static boolean checkReliable(String[] args) {
        for (int i = 0; i < args.length; ++i) {
            if (args[i].equals("-r")) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkWaitForAcks(String[] args) {
        for (int i = 0; i < args.length; ++i) {
            if (args[i].equals("-w")) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {

        System.out.println("Start Valve Publisher");
        boolean reliable = checkReliable(args);
        boolean waitForAcks = checkWaitForAcks(args);

        DomainParticipantFactory domainParticipantFactory = TheParticipantFactory.WithArgs(new StringSeqHolder(args));
        if (domainParticipantFactory == null) {
            System.err.println("ERROR: Domain Participant Factory not found");
            return;
        }
        DomainParticipant domainParticipant = domainParticipantFactory.create_participant(VALVE_PARTICIPANT,
                PARTICIPANT_QOS_DEFAULT.get(),
                null,
                DEFAULT_STATUS_MASK.value);
        if (domainParticipant == null) {
            System.err.println("ERROR: Domain Participant creation failed");
            return;
        }

        ValveStatusTypeSupportImpl valveStatusTypeSupport = new ValveStatusTypeSupportImpl();
        if (valveStatusTypeSupport.register_type(domainParticipant, "") != RETCODE_OK.value) {
            System.err.println("ERROR: register_type failed");
            return;
        }

        Topic topic = domainParticipant.create_topic(VALVE_TOPIC,
                valveStatusTypeSupport.get_type_name(),
                TOPIC_QOS_DEFAULT.get(),
                null,
                DEFAULT_STATUS_MASK.value);
        if (topic == null) {
            System.err.println("ERROR: Topic creation failed");
            return;
        }

        Publisher publisher = domainParticipant.create_publisher(PUBLISHER_QOS_DEFAULT.get(),
                null,
                DEFAULT_STATUS_MASK.value);
        if (publisher == null) {
            System.err.println("ERROR: Publisher creation failed");
            return;
        }

        // Use the default transport configuration (do nothing)

        DataWriterQos dataWriterQos = new DataWriterQos();
        dataWriterQos.durability = new DurabilityQosPolicy();
        dataWriterQos.durability.kind = DurabilityQosPolicyKind.from_int(0);
        dataWriterQos.durability_service = new DurabilityServiceQosPolicy();
        dataWriterQos.durability_service.history_kind = HistoryQosPolicyKind.from_int(0);
        dataWriterQos.durability_service.service_cleanup_delay = new Duration_t();
        dataWriterQos.deadline = new DeadlineQosPolicy();
        dataWriterQos.deadline.period = new Duration_t();
        dataWriterQos.latency_budget = new LatencyBudgetQosPolicy();
        dataWriterQos.latency_budget.duration = new Duration_t();
        dataWriterQos.liveliness = new LivelinessQosPolicy();
        dataWriterQos.liveliness.kind = LivelinessQosPolicyKind.from_int(0);
        dataWriterQos.liveliness.lease_duration = new Duration_t();
        dataWriterQos.reliability = new ReliabilityQosPolicy();
        dataWriterQos.reliability.kind = ReliabilityQosPolicyKind.from_int(0);
        dataWriterQos.reliability.max_blocking_time = new Duration_t();
        dataWriterQos.destination_order = new DestinationOrderQosPolicy();
        dataWriterQos.destination_order.kind = DestinationOrderQosPolicyKind.from_int(0);
        dataWriterQos.history = new HistoryQosPolicy();
        dataWriterQos.history.kind = HistoryQosPolicyKind.from_int(0);
        dataWriterQos.resource_limits = new ResourceLimitsQosPolicy();
        dataWriterQos.transport_priority = new TransportPriorityQosPolicy();
        dataWriterQos.lifespan = new LifespanQosPolicy();
        dataWriterQos.lifespan.duration = new Duration_t();
        dataWriterQos.user_data = new UserDataQosPolicy();
        dataWriterQos.user_data.value = new byte[0];
        dataWriterQos.ownership = new OwnershipQosPolicy();
        dataWriterQos.ownership.kind = OwnershipQosPolicyKind.from_int(0);
        dataWriterQos.ownership_strength = new OwnershipStrengthQosPolicy();
        dataWriterQos.writer_data_lifecycle = new WriterDataLifecycleQosPolicy();

        DataWriterQosHolder dataWriterQosHolder = new DataWriterQosHolder(dataWriterQos);
        publisher.get_default_datawriter_qos(dataWriterQosHolder);
        dataWriterQosHolder.value.history.kind = HistoryQosPolicyKind.KEEP_ALL_HISTORY_QOS;
        if (reliable) {
            dataWriterQosHolder.value.reliability.kind = ReliabilityQosPolicyKind.RELIABLE_RELIABILITY_QOS;
        }
        DataWriter dataWriter = publisher.create_datawriter(topic,
                dataWriterQosHolder.value,
                null,
                DEFAULT_STATUS_MASK.value);
        if (dataWriter == null) {
            System.err.println("ERROR: DataWriter creation failed");
            return;
        }
        System.out.println("Publisher Created DataWriter");

        StatusCondition statuscondition = dataWriter.get_statuscondition();
        statuscondition.set_enabled_statuses(PUBLICATION_MATCHED_STATUS.value);
        WaitSet waitSet = new WaitSet();
        waitSet.attach_condition(statuscondition);
        PublicationMatchedStatusHolder matched = new PublicationMatchedStatusHolder(new PublicationMatchedStatus());
        Duration_t timeout = new Duration_t(DURATION_INFINITE_SEC.value, DURATION_INFINITE_NSEC.value);

        while (true) {
            final int result = dataWriter.get_publication_matched_status(matched);
            if (result != RETCODE_OK.value) {
                System.err.println("ERROR: get_publication_matched_status()" + "failed.");
                return;
            }

            if (matched.value.current_count >= 1) {
                System.out.println("Publisher Matched");
                break;
            }

            ConditionSeqHolder conditionSeqHolder = new ConditionSeqHolder(new Condition[]{});
            if (waitSet.wait(conditionSeqHolder, timeout) != RETCODE_OK.value) {
                System.err.println("ERROR: wait() failed.");
                return;
            }
        }

        waitSet.detach_condition(statuscondition);

        ValveStatusDataWriter valveStatusDataWriter = ValveStatusDataWriterHelper.narrow(dataWriter);

        ValveStatus valveStatus = new ValveStatus();
        valveStatus.valveSerialNumber = 99; // key field
        valveStatus.timeStamp         = 0;
        valveStatus.stationNumber     = 0;
        valveStatus.cycleCountLimit   = 100;
        valveStatus.cycleCount        = 0;
        valveStatus.pressurePoint     = new Float(0.0);
        valveStatus.pressureFault     = PressureFaultType._PRESSURE_FAULT_N;
        valveStatus.detectedLeak      = LeakDetectedType._LEAK_DETECTED_N;
        valveStatus.input             = InputType._INPUT_N;
        int handle  = valveStatusDataWriter.register_instance(valveStatus); // register key field

        int ret = RETCODE_TIMEOUT.value;
        for (; valveStatus.cycleCount < N_MSGS; ++valveStatus.cycleCount) {
            while ((ret = valveStatusDataWriter.write(valveStatus, handle)) == RETCODE_TIMEOUT.value) {
            }
            if (ret != RETCODE_OK.value) {
                System.err.println("ERROR " + valveStatus.cycleCount + " write() returned " + ret);
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException ie) {
            }
        }

        if (waitForAcks) {
            System.out.println("Publisher waiting for acks");

            // Wait for acknowledgements
            Duration_t forever = new Duration_t(DURATION_INFINITE_SEC.value, DURATION_INFINITE_NSEC.value);
            dataWriter.wait_for_acknowledgments(forever);
        } else {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ie) {
            }
        }
        System.out.println("Stop Publisher");

        // Clean up
        domainParticipant.delete_contained_entities();
        domainParticipantFactory.delete_participant(domainParticipant);
        TheServiceParticipant.shutdown();

        System.out.println("Publisher exiting");
    }
}
