# Marriage Law Q&A System
Traditional legal consulting services have long faced the dual dilemma of **lagging response time** and **inefficient information retrieval**. When users encounter urgent legal issues, they often have to endure long waiting times in queues; similarly, lawyers also need to spend considerable time conducting manual searches through vast amounts of regulations, precedents, and judicial interpretations when handling cases. This inefficient service model not only increases the time cost for users but also hinders the digital transformation and upgrading of the legal service industry. To address this common challenge in the industry, this project has developed an intelligent legal question-answering system based on the deep integration of the **Qwen-max large model** and the **RAG retrieval-augmented generation architecture**, aiming to build a new generation of legal intelligent service platform that combines professional depth with response speed.

![image-20251106162048407](图片1.jpg)

## Core Technical Architecture
#### Base model: Qwen-max large language model
The system utilizes **Qwen-max** from Alibaba Cloud's Tongyi Qianwen series as its core cognitive engine. This model possesses the following advantages:
- **Strong Chinese comprehension ability**: Deeply optimized for complex expressions, professional terminology, and logical structure of Chinese legal texts
- **Long text processing capability**: Supports extremely long context windows, enabling the parsing of complete contract texts, judgments, or legal opinions in one go
- **Controllable knowledge boundary**: Effectively constraining the generation scope through the RAG architecture to avoid the common "illusion" problem of large models, ensuring the rigor of legal advice
#### Agent: LangChain4j provides AgentService to implement agent construction
The management of various components, including context management and tool invocation, is automatically implemented by the Agent. This implementation approach has the following advantages:
- **Low-code implementation**: Context management can be easily achieved by directly utilizing classes like ChatMessage provided by LangChain4j
- **Easy component assembly**: Tools can be quickly and easily declared and invoked by the Agent through the .tools() method of the Agent builder or advanced methods such as @Tool
#### Hybrid Search: Elasticsearch Vector Search and Keyword Search
The combination of Knn vector retrieval provided by the Elasticsearch database and keyword retrieval, along with the Reranker re-ranking model, achieves efficient and accurate retrieval. The advantages are as follows:
- **Ease of implementation**: Elasticsearch natively supports both Knn vector retrieval and keyword retrieval
- **Efficiency and accuracy coexist**: The recall and precision of hybrid retrieval can reach over 90%
![image-20251106162048407](图片2.png)
![image-20251106162048407](图片3.png)