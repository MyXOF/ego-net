import json, codecs, copy
from config.config import *


class NodeManager:
    _node_info = None
    _node_graph = None
    _edge_info = None
    _edge_graph = None
    _graph_all = None

    _ego_net_info = None
    _ego_net_points = set([])
    _ego_delete_points = set([])

    @classmethod
    def load_info(cls):
        if not cls._node_graph:
            cls._node_info = {}
            cls._node_graph = json.load(codecs.open(NODE_DATA_PATH, 'r', 'utf-8-sig'))
            for node in cls._node_graph['nodes']:
                cls._node_info[node['id']] = node

        if not cls._edge_graph:
            cls._edge_graph = json.load(codecs.open(EDGE_DATA_PATH, 'r', 'utf-8-sig'))

        cls._graph_all = dict()
        cls._graph_all['nodes'] = cls._node_graph['nodes']
        cls._graph_all['edges'] = cls._edge_graph['edges']

        if not cls._ego_net_info:
            cls._ego_net_info = json.load(codecs.open(EGO_NET_DARA_PATH, 'r', 'utf-8-sig'))
            pass
        pass

    @classmethod
    def get_graph(cls):
        if not cls._graph_all:
            cls.load_info()
        cls.clear_ego_net()
        return cls._graph_all

    @classmethod
    def clear_ego_net(cls):
        cls._ego_net_points = set([])
        cls._ego_delete_points = set([])
        pass

    @classmethod
    def get_ego_net(cls, ego_node, oper_type):
        if oper_type == "graph-all":
            return cls.get_graph()

        cls.load_info()
        if ego_node not in cls._node_info:
            return cls.get_graph()
        if oper_type == "add-ego":
            cls._ego_net_points.add(ego_node)
            if ego_node in cls._ego_delete_points:
                cls._ego_delete_points.remove(ego_node)
        elif oper_type == "remove-ego":
            cls._ego_delete_points.add(ego_node)

        ego_net_graph = {'nodes': [], 'edges': []}
        node_set = set([])
        for node in cls._ego_net_points:
            edges = cls._ego_net_info[node]
            for line in edges:
                source_id = line['sourceID']
                target_id = line['targetID']
                if source_id not in cls._ego_delete_points and target_id not in cls._ego_delete_points:
                    ego_net_graph['edges'].append(line)

                if source_id not in node_set:
                    origin_node_info = copy.deepcopy(cls._node_info[source_id])
                    if source_id in cls._ego_net_points:
                        origin_node_info['color'] = '#AD5A5A'
                    if source_id in cls._ego_delete_points:
                        origin_node_info['color'] = '#8E8E8E'
                    ego_net_graph['nodes'].append(origin_node_info)
                    node_set.add(source_id)
                if target_id not in node_set:
                    origin_node_info = copy.deepcopy(cls._node_info[target_id])
                    if target_id in cls._ego_net_points:
                        origin_node_info['color'] = '#AD5A5A'
                    if target_id in cls._ego_delete_points:
                        origin_node_info['color'] = '#8E8E8E'
                    ego_net_graph['nodes'].append(origin_node_info)
                    node_set.add(target_id)
            pass

        if len(ego_net_graph['nodes']) == 0:
            return cls.get_graph()

        return ego_net_graph
        pass


if __name__ == "__main__":
    tmp = NodeManager.get_ego_net('34', 'add')
    print(tmp)
    pass
